//Check ok
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <sys/wait.h>
#include <fcntl.h>
#include <sys/stat.h>
#include <errno.h>

char *color_blue = "\033[01;34m";
char *color_end = "\033[00m";

void error(char *m) {
    write(2, m, strlen(m));
    write(2, "\n", 1);
    exit(0);
}

int main(int arc, char *arv[]) {
    int i, n, st;
    char s[100];
    pid_t *pids;
    char filenames[80][80]; // Store the file names
    char *args[] = {"player3", NULL};
    int winner_pid = -1;
    float winner_score = -1;
    int all_equal = 1; // Flag to check if all scores are equal

    if (arc != 2)
        error("Error: wrong arguments.");

    n = atoi(arv[1]); // Number of players
    pids = malloc(n * sizeof(pid_t));
    if (pids == NULL)
        error("Error: memory allocation failed.");

    sprintf(s, "\n**********Start of game (%d players, pid croupier=%d)***********\n", n, getpid());
    if (write(1, s, strlen(s)) < 0)
        error("write");

    // Create child processes
    for (i = 0; i < n; i++) {
        switch (pids[i] = fork()) {
            case -1:
                error("fork");

            case 0: {
                // Child process: use its own PID and the croupier's PID
                sprintf(filenames[i], "/tmp/%d.%d", getppid(), getpid());
                execlp("./player3", "player3", filenames[i], NULL);
                error("exec");
            }

            default: {
                // Parent process (croupier): register the file name
                sprintf(filenames[i], "/tmp/%d.%d", getpid(), pids[i]);
                sprintf(s, "%s[%d] pid=%d created%s\n", color_blue, getpid(), pids[i], color_end);
                if (write(1, s, strlen(s)) < 0)
                    error("write");
            }
        }
    }

    // Initial calculation of the winner using output values
    for (i = 0; i < n; i++) {
        pid_t pid = waitpid(pids[i], &st, 0);
        if (pid == -1)
            error("wait");

        int fd = open(filenames[i], O_RDONLY);
        if (fd < 0)
            error("Error opening file");

        char buffer[10];
        int bytes_read = read(fd, buffer, sizeof(buffer) - 1);
        if (bytes_read < 0)
            error("Error reading file");

        buffer[bytes_read] = '\0';
        float file_score = atof(buffer); // Convert the score to float
        close(fd);

        sprintf(s, "%s[%d] pid=%d ended, %.1f points (file)%s\n", color_blue, getpid(), pid, file_score, color_end);
        if (write(1, s, strlen(s)) < 0)
            error("write");

        if (file_score <= 7.5 && (file_score > winner_score || (file_score == winner_score && pid < winner_pid))) {
            winner_pid = pid;
            winner_score = file_score;
            all_equal = 0;
        }
    }

    sprintf(s, "\n**********End of game: all players have ended***********\n");
    if (write(1, s, strlen(s)) < 0)
        error("write");

    if (winner_score == -1) {
        sprintf(s, "**********And the winner is NOBODY**********************\n");
    } else {
        sprintf(s, "**********And the winner is pid=%d (%.1f points)**********************\n", winner_pid, winner_score);
    }

    if (write(1, s, strlen(s)) < 0)
        error("write");

    // Second verification using files (N-FILES)
    sprintf(s, "\n**********USING N-FILES**********\n");
    if (write(1, s, strlen(s)) < 0)
        error("write");

    winner_pid = -1;
    winner_score = -1;
    all_equal = 1;

    for (i = 0; i < n; i++) {
        int fd = open(filenames[i], O_RDONLY);
        if (fd < 0)
            error("Error opening file");

        char buffer[10];
        int bytes_read = read(fd, buffer, sizeof(buffer) - 1);
        if (bytes_read < 0)
            error("Error reading file");

        buffer[bytes_read] = '\0';
        float file_score = atof(buffer); // Convert the score to float
        close(fd);

        sprintf(s, "%s[%d] pid=%d ended, %.1f points%s\n", color_blue, getpid(), pids[i], file_score, color_end);
        if (write(1, s, strlen(s)) < 0)
            error("write");

        if (file_score <= 7.5 && (file_score > winner_score || (file_score == winner_score && pids[i] < winner_pid))) {
            winner_pid = pids[i];
            winner_score = file_score;
            all_equal = 0;
        }

        unlink(filenames[i]); // Delete the file
    }

    if (winner_score == -1) {
        sprintf(s, "**********And the winner is NOBODY**********************\n");
    } else {
        sprintf(s, "**********And the winner is pid=%d (%.1f points)**********************\n", winner_pid, winner_score);
    }

    if (write(1, s, strlen(s)) < 0)
        error("write");

    free(pids);
    exit(0);
}
