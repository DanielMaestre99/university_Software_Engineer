

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

int main(int argc, char *argv[]) {
    int i, n, st;
    char s[100];
    pid_t *pids;
    char shared_file[80];
    int winner_pid = -1;
    float winner_score = -1;
    int all_equal = 1; // Flag to check if all scores are equal

    if (argc != 2)
        error("Error: wrong arguments.");

    n = atoi(argv[1]);
    pids = malloc(n * sizeof(pid_t));
    if (pids == NULL)
        error("Error: memory allocation failed.");

    // Create a shared file in /tmp
    sprintf(shared_file, "/tmp/%d", getpid());
    int fd = open(shared_file, O_CREAT | O_TRUNC | O_RDWR, 0666);
    if (fd < 0)
        error("Error creating shared file");
    close(fd);

    sprintf(s, "\n**********Start of game (%d players, pid croupier=%d)***********\n", n, getpid());
    if (write(1, s, strlen(s)) < 0)
        error("write");

    // Create player processes
    for (i = 0; i < n; i++) {
        switch (pids[i] = fork()) {
            case -1:
                error("fork");

            case 0: {
                // Child process executes player program
                execlp("./player4", "player4", shared_file, NULL);
                error("exec");
            }

            default: {
                // Log player creation
                sprintf(s, "%s[%d] pid=%d created%s\n", color_blue, getpid(), pids[i], color_end);
                if (write(1, s, strlen(s)) < 0)
                    error("write");
            }
        }
    }

    // Wait for all players to finish
    for (i = 0; i < n; i++) {
        pid_t pid = waitpid(pids[i], &st, 0);
        if (pid == -1)
            error("wait");
    }

    // Read scores from the shared file
    fd = open(shared_file, O_RDONLY);
    if (fd < 0)
        error("Error opening shared file");

    char buffer[256];
    while (read(fd, buffer, sizeof(buffer)) > 0) {
        char *line = strtok(buffer, "\n");
        while (line != NULL) {
            int player_pid;
            float file_score;
            sscanf(line, "%d %f", &player_pid, &file_score);

            sprintf(s, "%s[%d] pid=%d ended, %.1f points (file)%s\n", color_blue, getpid(), player_pid, file_score, color_end);
            if (write(1, s, strlen(s)) < 0)
                error("write");

            // Determine winner
            if (file_score <= 7.5 && (file_score > winner_score || (file_score == winner_score && player_pid < winner_pid))) {
                winner_pid = player_pid;
                winner_score = file_score;
            }

            line = strtok(NULL, "\n");
        }
    }
    close(fd);

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

    // Using "USING 1 FILE" section
    sprintf(s, "\n**********USING 1 FILE**********\n");
    if (write(1, s, strlen(s)) < 0)
        error("write");

    fd = open(shared_file, O_RDONLY);
    if (fd < 0)
        error("Error reopening shared file");

    while (read(fd, buffer, sizeof(buffer)) > 0) {
        char *line = strtok(buffer, "\n");
        while (line != NULL) {
            int player_pid;
            float file_score;
            sscanf(line, "%d %f", &player_pid, &file_score);

            sprintf(s, "%s[%d] pid=%d ended, %.1f points%s\n", color_blue, getpid(), player_pid, file_score, color_end);
            if (write(1, s, strlen(s)) < 0)
                error("write");

            line = strtok(NULL, "\n");
        }
    }
    close(fd);

    unlink(shared_file);
    free(pids);
    exit(0);
}
