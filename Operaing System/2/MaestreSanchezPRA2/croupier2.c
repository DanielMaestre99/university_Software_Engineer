//Check ok
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <sys/wait.h>

// Colors for output
char *color_blue = "\033[01;34m";
char *color_end = "\033[00m";

void error(char *m) {
    write(2, m, strlen(m));
    write(2, "\n", 1);
    exit(1);
}

int main(int argc, char *argv[]) {
    int i, n, st;
    pid_t *pids;  // Dynamic pointers to store PIDs
    char s[100];
    pid_t winner_pid = -1;
    int winner_score = -1;

    if (argc != 2) {
        error("Error: wrong arguments.");
    }

    n = atoi(argv[1]);

    // Allocate memory to store the PIDs of the players
    pids = malloc(n * sizeof(pid_t));
    if (pids == NULL) {
        error("Error: memory allocation failed.");
    }

    // Start of the game
    sprintf(s, "\n**********Start of game (%d players, pid croupier=%d)***********\n", n, getpid());
    if (write(1, s, strlen(s)) < 0) {
        error("write");
    }

    // Create child processes for each player
    for (i = 0; i < n; i++) {
        switch (pids[i] = fork()) {
        case -1:
            error("fork");

        case 0:  // Child process
            if (write(1, "exec\n", 5) < 0) {
                error("write");
            }
            char *args[] = {"./player2", NULL};
            execv(args[0], args);
            error("exec");

        default:  // Parent process
            sprintf(s, "%s[%d] pid=%d created%s\n", color_blue, getpid(), pids[i], color_end);
            if (write(1, s, strlen(s)) < 0) {
                error("write");
            }
        }
    }

    // Wait for all players and calculate the winner
    for (i = 0; i < n; i++) {
        pid_t pid = waitpid(pids[i], &st, 0);
        if (pid == -1) {
            error("wait");
        }

        int score = WEXITSTATUS(st);  // Get the player's score
        sprintf(s, "%s[%d] pid=%d ended, %.1f points%s\n", color_blue, getpid(), pid, score / 2.0, color_end);
        if (write(1, s, strlen(s)) < 0) {
            error("write");
        }

        // Determine if this player is the winner
        if (winner_score == -1 || score > winner_score || (score == winner_score && pid < winner_pid)) {
            winner_score = score;
            winner_pid = pid;
        }
    }

    // End of the game
    sprintf(s, "\n**********End of game: all players have ended***********\n");
    if (write(1, s, strlen(s)) < 0) {
        error("write");
    }

    if (winner_score == -1 || winner_score == 0) {
        sprintf(s, "**********And the winner is NOBODY**********************\n");
    } else {
        sprintf(s, "Winner: pid=%d with score=%.1f\n", winner_pid, winner_score / 2.0);
    }

    if (write(1, s, strlen(s)) < 0) {
        error("write");
    }

    // Free dynamic memory
    free(pids);

    exit(0);
}
