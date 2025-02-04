//Check ok

#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>



void error(char *m) {
    write(2, m, strlen(m));
    write(2, "\n", 1);
    exit(1);
}

int main() {
    int current_score = 0;

    exit(current_score);         // sent the score
}
