# Dynamic Programming (DP)

DP is used to solve problems with overlapping subproblems and optimal substructure.

## Core Concepts
- **Memoization**: Top-down approach. Cache results of recursive calls.
- **Tabulation**: Bottom-up approach. Fill a table (array) iteratively.

## Classic Problems
1. **Knapsack Problem**: Selecting items to maximize value without exceeding weight.
2. **Longest Common Subsequence (LCS)**: Finding the longest subsequence common to two strings.
3. **Fibonacci Sequence**: The simplest DP example.

## Steps to Solve
1. Define the state (e.g., `dp[i]`).
2. Find the transition equation (e.g., `dp[i] = dp[i-1] + dp[i-2]`).
3. Set base cases.
