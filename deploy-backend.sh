#!/bin/bash
# Deploys the backend services that changed since the last deploy.
# GitHub Actions runs it on EC2 right after `git pull` (see .github/workflows/deploy.yml).
#
# One-time setup on the server, so the script knows what is running now:
#     cd ~/stadium-booking-microservices && git rev-parse HEAD > ~/.backend-deployed-commit
#
# The Java services are stopped while Maven builds, because the 2 GB t3.small
# freezes otherwise, and are started again in stages to avoid a memory spike.
# If the build fails, the old images are started again so the site is not left down.

set -Eeuo pipefail

cd "${BACKEND_DIR:-$HOME/stadium-booking-microservices}"

STATE="${STATE_FILE:-$HOME/.backend-deployed-commit}"
WAIT="${STAGE_WAIT:-45}"

TO=$(git rev-parse HEAD)
FROM=$(cat "$STATE" 2>/dev/null || true)

if [ -z "$FROM" ]; then
    echo "Missing $STATE. Run once:  git rev-parse HEAD > $STATE"
    exit 1
fi

if [ "$FROM" = "$TO" ]; then
    echo "Nothing new to deploy ($TO)"
    exit 0
fi

# ---------------------------------------------------------------
# Which Java services exist (a folder with a pom.xml and a compose service of the same name)
# ---------------------------------------------------------------
COMPOSE_SERVICES=$(docker compose config --services)
ALL=""
for d in */; do
    d=${d%/}
    if [ -f "$d/pom.xml" ] && [ "$d" != "stadium-common" ] && echo "$COMPOSE_SERVICES" | grep -qx "$d"; then
        ALL="$ALL $d"
    fi
done
ALL=${ALL# }

# ---------------------------------------------------------------
# Which of them changed
# ---------------------------------------------------------------
if CHANGED=$(git diff --name-only "$FROM" "$TO" 2>/dev/null); then
    :
else
    echo "Cannot compare with $FROM, rebuilding everything"
    CHANGED="stadium-common/"
fi

SVC=""
if echo "$CHANGED" | grep -qE '^(stadium-common/|pom\.xml$)'; then
    SVC="$ALL"
else
    for s in $ALL; do
        if echo "$CHANGED" | grep -q "^$s/"; then
            SVC="$SVC $s"
        fi
    done
    SVC=${SVC# }
fi

if [ -z "$SVC" ]; then
    echo "No backend service changed, nothing to rebuild"
    echo "$TO" > "$STATE"
    exit 0
fi

echo "Services to rebuild: $SVC"

# ---------------------------------------------------------------
# Start services in stages, like the manual memory-safe procedure
# ---------------------------------------------------------------
start_group() {
    local list=""
    for s in "$@"; do
        if echo " $ALL " | grep -q " $s "; then
            list="$list $s"
        fi
    done
    if [ -n "$list" ]; then
        docker compose up -d $list
    fi
}

start_staged() {
    start_group discovery-server;            sleep "$WAIT"
    start_group config-server;               sleep "$WAIT"
    start_group api-gateway auth-service;    sleep "$WAIT"

    local rest="" n=0
    for s in $ALL; do
        case $s in
            discovery-server|config-server|api-gateway|auth-service) ;;
            *) rest="$rest $s" ;;
        esac
    done
    for s in $rest; do
        docker compose up -d "$s"
        n=$((n + 1))
        if [ $((n % 2)) -eq 0 ]; then sleep 30; fi
    done
}

recover() {
    echo "DEPLOY FAILED: starting the previous version again"
    start_staged || true
    exit 1
}
trap recover ERR

# ---------------------------------------------------------------
# Build
# ---------------------------------------------------------------
docker compose stop $ALL

export MAVEN_OPTS="-Xmx400m"
PL=$(echo "$SVC" | tr ' ' ',')
mvn -B --no-transfer-progress clean package -DskipTests -pl "$PL" -am

docker compose build $SVC

# ---------------------------------------------------------------
# Start, remember what is deployed
# ---------------------------------------------------------------
trap - ERR
start_staged

# nginx in the frontend container looks up the gateway at start-up, so refresh it
docker compose restart frontend || true

echo "$TO" > "$STATE"
docker image prune -f
docker compose ps
echo "Backend deployed: $SVC"
