#!/bin/bash

set -u

ROOT="$(cd "$(dirname "$0")" && pwd)"
RUNNER="$ROOT/test_code/othellostart.sh"
MY_PLAYER="$ROOT/othello.sh"
NAIVE_PLAYER="$ROOT/test_code/othello_naive.sh"

LOG="$ROOT/results.log"
CSV="$ROOT/results.csv"

echo "side,time_limit,winner,margin,result" > "$CSV"

run_match() {
    side="$1"
    time_limit="$2"

    if [ "$side" = "white" ]; then
        white="$MY_PLAYER"
        black="$NAIVE_PLAYER"
    else
        white="$NAIVE_PLAYER"
        black="$MY_PLAYER"
    fi

    echo "Running: $side, ${time_limit}s" | tee -a "$LOG"

    output=$("$RUNNER" "$white" "$black" "$time_limit" 2>&1)
    printf '%s\n' "$output" >> "$LOG"

    result=$(printf '%s\n' "$output" | awk -v side="$side" '
        /won with/ {
            winner=$1
            margin=$4
        }
        END {
            if (winner == side || (side == "white" && winner == "White") ||
                (side == "black" && winner == "Black")) {
                result="win"
            } else {
                result="loss"
            }
            printf "%s,%s,%s", winner, margin, result
        }
    ')

    echo "$side,$time_limit,$result" >> "$CSV"
    echo "$result" | tee -a "$LOG"
    echo | tee -a "$LOG"
}

: > "$LOG"

for time_limit in $(seq 2 10); do
    run_match white "$time_limit"
done

for time_limit in $(seq 2 10); do
    run_match black "$time_limit"
done

echo "Tournament complete."
echo "Detailed log: $LOG"
echo "CSV summary:  $CSV"

echo
echo "Summary:"
awk -F, '
    NR > 1 {
        games++
        if ($5 == "win") wins++
        else losses++
    }
    END {
        printf "Games: %d\nWins: %d\nLosses: %d\n", games, wins, losses
    }
' "$CSV"