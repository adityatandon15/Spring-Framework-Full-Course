#include <iostream>
#include <iomanip>
#include <algorithm>
using namespace std;

/*
================================================================================
  Codeforces 578C - Weakness and Poorness
  https://codeforces.com/contest/578/problem/C
================================================================================

  PROBLEM (in simple words):
  --------------------------
  You are given an array: a[1], a[2], ..., a[n]

  You choose ONE real number x, and make a new array:
      b[i] = a[i] - x

  Definitions from the problem:
      Poorness of a segment  = |sum of elements in that segment|
      Weakness of the array  = maximum poorness over ALL segments

  Goal:
      Find the best x so that weakness becomes as SMALL as possible.
      Print that minimum possible weakness.


  MATH FORM of one segment (your idea):
  -------------------------------------
  Let pre[0] = 0
      pre[k] = a[1] + a[2] + ... + a[k]     (prefix of ORIGINAL array)

  Take a segment from index (i+1) to j   (so length = j - i)

  Sum after subtracting x from every element:
      sum_b = (a[i+1]-x) + (a[i+2]-x) + ... + (a[j]-x)
            = (pre[j] - pre[i]) - (j - i) * x

  Same thing with opposite sign (your form):
      F_{i,j}(x) = pre[i] - pre[j] + (j - i) * x
                 = - sum_b

  Poorness of that segment = |sum_b| = |F_{i,j}(x)|

  So for ONE fixed pair (i, j), F is just a straight LINE in x:
      F(x) = (j - i) * x + (pre[i] - pre[j])
             \_____/         \______________/
             slope           constant


  WEAKNESS as a function of x:
  ----------------------------
  Weakness(x) = MAX over all pairs i < j of |F_{i,j}(x)|

                = max_{0 <= i < j <= n} | pre[i] - pre[j] + (j - i) * x |

  Yes: for a fixed x, we take the MAXIMUM |F| over all segments.


  WHAT WE NEED TO SOLVE:
  ----------------------
  Answer = min over x of  Weakness(x)
         = min_x  max_{i<j} | pre[i] - pre[j] + (j-i)*x |

  This is a minimax problem:
      - inside:  MAX of absolute values of many lines
      - outside: MIN over the choice of x


  FAST WAY to compute Weakness(x) for one x:
  ------------------------------------------
  Building prefix of b[k] = a[k] - x, then:

      Weakness(x) = maxPrefix_b - minPrefix_b

  This is O(n) and equal to max |F_{i,j}(x)| over all i < j.
  (No need to loop all pairs — that would be O(n^2) and too slow.)


  WHY TERNARY SEARCH?:
  --------------------
  If you plot weakness(x) as x changes, the graph goes down then up
  (like a U shape / unimodal / convex function).

  So we can ternary-search on x to find the lowest point of that U.
  Search range for x: [-10000, 10000]  (because |a[i]| <= 10000)

  After many steps, left and right become almost the same best x.
  Then we print weakness of that x.


  HOW weakness(x) LOOKS when x changes (for array 1 2 3):
  -------------------------------------------------------
  x:          0     1     2     3     4
  weakness:   6     3     1     3     6

  Graph (height = weakness):

      weakness
         ^
       6 | *                       *
       5 |
       4 |
       3 |       *           *
       2 |
       1 |             *   <--- minimum (best x)
       0 +-----+-----+-----+-----+-----> x
         0     1     2     3     4
                       ^
                    best x = 2

  Shape: HIGH -> goes DOWN -> reaches BOTTOM -> goes UP again
  That is why ternary search works (one valley only).


  EXAMPLE:
  --------
  Input:  1 2 3
  Best x = 2
  New array b = -1, 0, 1
  Max |segment sum| = 1
  Answer = 1.0
================================================================================
*/

// Set to 1 to also print a learning graph on stderr (does not change the answer line)
// Keep 0 for normal Codeforces-style input/output
#define SHOW_GRAPH 0

const int MAX_N = 200005;

int n;                 // length of the array (1 <= n <= 200000)
double arr[MAX_N];     // the given sequence a[i]

/*
  getScore(x) = weakness of the sequence (a[i] - x)

  Steps:
    1. Imagine every element becomes (arr[i] - x)
    2. Build running / prefix sums of that new sequence
    3. Track the minimum and maximum prefix values
    4. Return (maxPrefix - minPrefix)
       => this equals the maximum |segment sum|
       => this is exactly the WEAKNESS for this x

  Smaller score  =>  better x
*/
double getScore(double x) {
    double runningSum = 0;   // current prefix sum of (arr[i] - x)
    double minPrefix = 0;    // smallest prefix so far (include empty prefix = 0)
    double maxPrefix = 0;    // largest prefix so far

    for (int i = 0; i < n; i++) {
        // move one step in the prefix: add next element after subtracting x
        runningSum += arr[i] - x;

        // update extremes of the prefix array
        minPrefix = min(minPrefix, runningSum);
        maxPrefix = max(maxPrefix, runningSum);
    }

    // max |segment sum| = difference between highest and lowest prefix
    return maxPrefix - minPrefix;
}

/*
  visualizeWeakness()
  -------------------
  Prints weakness(x) for many values of x, so you can SEE the U-shape.

  Output has two parts:
    1) Table:   x   |   weakness(x)
    2) ASCII bars: longer bar = larger weakness

  For array [1, 2, 3], you will see weakness smallest near x = 2.
*/
void visualizeWeakness(double fromX, double toX, int points) {
    // Printed to cerr so it never mixes with the official answer on cout
    cerr << "\n========== VISUALIZE weakness(x) ==========\n";
    cerr << "Array: ";
    for (int i = 0; i < n; i++) {
        cerr << arr[i] << (i + 1 == n ? '\n' : ' ');
    }

    cerr << fixed << setprecision(4);
    cerr << "\n   x        weakness(x)     bar\n";
    cerr << "-------------------------------------------\n";

    double maxW = 0;
    for (int i = 0; i <= points; i++) {
        double x = fromX + (toX - fromX) * i / points;
        maxW = max(maxW, getScore(x));
    }

    int bestIndex = 0;
    double bestWeakness = 1e100;

    for (int i = 0; i <= points; i++) {
        double x = fromX + (toX - fromX) * i / points;
        double w = getScore(x);

        if (w < bestWeakness) {
            bestWeakness = w;
            bestIndex = i;
        }

        int barLen = 0;
        if (maxW > 0) {
            barLen = (int)(40.0 * w / maxW);
        }

        cerr << setw(8) << x << "   "
             << setw(10) << w << "     ";
        for (int b = 0; b < barLen; b++) {
            cerr << '#';
        }
        cerr << "\n";
    }

    double bestX = fromX + (toX - fromX) * bestIndex / points;
    cerr << "\nAmong these sample points:\n";
    cerr << "  best x        ≈ " << bestX << "\n";
    cerr << "  min weakness ≈ " << bestWeakness << "\n";
    cerr << "===========================================\n\n";
}

int main() {
    /*
      INPUT format (Codeforces):
        Line 1: n
        Line 2: a1 a2 ... an

      Example:
        3
        1 2 3

      OUTPUT format:
        One real number = minimum weakness
        Example:
        1.000000000000000
    */

    // ----- read input -----
    cin >> n;
    for (int i = 0; i < n; i++) {
        cin >> arr[i];
    }

#if SHOW_GRAPH
    double lo = arr[0], hi = arr[0];
    for (int i = 1; i < n; i++) {
        lo = min(lo, arr[i]);
        hi = max(hi, arr[i]);
    }
    visualizeWeakness(lo - 1.0, hi + 1.0, 20);
#endif

    // ----- ternary search for best x -----
    // Possible values of x lie in this range (|a[i]| <= 10000)
    double left = -10000;
    double right = 10000;

    /*
      Ternary search on x:
      --------------------
      We pick two points mid1 and mid2 inside [left, right].

      Compare weakness at those points:
        - if mid1 is better (smaller weakness), throw away the right side
        - otherwise throw away the left side

      Repeat many times until the interval is tiny.
      Then any point in [left, right] is almost the optimal x.

      Note:
      Classic equal-thirds formula is:
          mid1 = left + (right - left) / 3
          mid2 = right - (right - left) / 3

      Below we use another valid way to choose two points
      (at about 50% and 75%). Both work for this unimodal function.
    */
    for (int step = 0; step < 100; step++) {
        double mid1 = (left + right) / 2.0;
        double mid2 = (mid1 + right) / 2.0;

        if (getScore(mid1) < getScore(mid2)) {
            right = mid2;
        } else {
            left = mid1;
        }
    }

    // ----- print output (one real number) -----
    // high precision; judge allows absolute/relative error <= 1e-6
    cout << fixed << setprecision(15) << getScore(left) << "\n";

    return 0;
}
