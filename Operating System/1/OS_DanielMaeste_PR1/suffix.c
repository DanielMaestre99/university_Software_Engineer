#include <stdlib.h>
#include <errno.h>
#include <stdio.h>
#include <string.h>
#include <math.h>

char **suffix;

void
panic (char *m)
{
  fprintf (stderr, "%s\n", m);
  exit (0);
}

int
main (int argc, char *argv[])
{
  int n, i;

  if (argc != 2)
    panic ("wrong parameters");

/* Your code starts here */

char *input_string = argv[1];
  n = strlen(input_string);

  // Allocate memory for the pointer array
  suffix = malloc(n * sizeof(char *));
  if (suffix == NULL) {
      panic("Memory allocation failed for suffix array");
  }

  // Create and allocate memory for each suffix
  for (i = 0; i < n; i++) {
      suffix[i] = malloc((n - i + 1) * sizeof(char)); // +1 for null terminator
      if (suffix[i] == NULL) {
          panic("Memory allocation failed for suffix string");
      }
      strcpy(suffix[i], input_string + i); // Copy the suffix from location i
  }

/* Your code ends here */
  for (i = 0; i < n; i++)
    {
      printf ("%d %s\n", i, suffix[i]);
    }

  return 0;
}
