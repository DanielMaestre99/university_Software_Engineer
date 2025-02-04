#include <stdio.h>
#include <string.h>

#define BUFFER_SIZE 8

struct {
    char input[BUFFER_SIZE];
    char test[BUFFER_SIZE];
} data;

void clear_input_buffer()
{
    int ch;
    while ((ch = getchar()) != '\n' && ch != EOF);
}

int main(int argc, char *argv[])
{
    // Initialise the correct password
    snprintf(data.test, BUFFER_SIZE, "uoc");

    do
    {
        printf("Passwd? ");
        
        // We use fgets to read the user's complete input.
        if (fgets(data.input, BUFFER_SIZE, stdin) != NULL)
        {
            // If the entry was longer than BUFFER_SIZE - 1, clear the rest of the buffer.
            if (data.input[strlen(data.input) - 1] != '\n') {
                clear_input_buffer();
            }
            
            // Remove line break at the end if present
            data.input[strcspn(data.input, "\n")] = '\0';
        }
    }
    while (strcmp(data.input, data.test) != 0);

    printf("Passwd OK\n");
    return 0;
}
