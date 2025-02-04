//Check ok
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <sys/wait.h>

char *color_blue = "\033[01;34m";
char *color_end = "\033[00m";

void
error (char *m)
{
  write (2, m, strlen (m));
  write (2, "\n", 1);
  exit (0);
}

int
main (int arc, char *arv[])
{
  int i, n, st;
  char s[100];
  pid_t *pids;
  char *args[] = { "player1", NULL };

  if (arc != 2)
    error ("Error: wrong arguments.");

  n = atoi (arv[1]);

  pids = malloc(n * sizeof(pid_t));
  if (pids == NULL)
    error ("Error: memory allocation failed.");

  sprintf (s,
           "\n**********Start of game (%d players, pid croupier=%d)***********\n",
           n, getpid());
  if (write (1, s, strlen (s)) < 0)
    error ("write");

  for (i = 0; i < n; i++)
    {
      switch (pids[i] = fork())
        {
        case -1:
          error ("fork");

        case 0:
          execv (args[0], args);
          error ("exec");

        default:
          sprintf (s, "%s[%d] pid=%d created%s\n", color_blue, getpid(), pids[i],
                   color_end);
          if (write (1, s, strlen (s)) < 0)
            error ("write");
        }
    }

  for (i = 0; i < n; i++)
    {
      pid_t pid = waitpid(pids[i], &st, 0);
      if (pid == -1)
        error ("wait");

      sprintf (s, "%s[%d] pid=%d ended%s\n", color_blue, getpid(), pid,
               color_end);
      if (write (1, s, strlen (s)) < 0)
        error ("write");
    }

  free(pids);

  sprintf (s, "\n**********End of game: all players have ended***********\n");
  if (write (1, s, strlen (s)) < 0)
    error ("write");

  exit (0);
}
