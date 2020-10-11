/*
https://github.com/wxarmstrong/2020_FindSum

William Armstrong
CS 3310
Cal Poly Pomona
Fall 2020
10/11/2020

Midterm Project #2
*/

/*
COMPLEXITY ANALYSIS:
======================================================
Unsorted: Brute Force

Time:
 Key operation : Comparison (num1 + num2 == sum)
 Best case     : O(1) as only the first two values need to be checked.
 Worst case    : If no solution exists, every entry in the array will be checked against every other entry.
                 Hence,
				 W(2) = 1
				 W(n) = W(n-1) + n - 1
				 ...
				 W(n) = O(n^2)
 Average case  : The average number of comparisons the first integer has to make with each remaining integer
                 in the array is in O(n), and that has to be multiplied by the # of instances of the first integer
				 (also in O(n)). Hence,
				 A(n) = O(n^2)
------------------------------------------------------
Memory:
 All cases: O(1)
======================================================
Unsorted: Brute Force w/ MinMax

The # of key operations performed is equal to or less than those in the previous example. Hence the upper bounds still hold:
B(n) = O(1)
W(n) = O(n^2)
A(n) = O(n^2)

I suspect it can be shown that the reduced # of operations results in a slightly tighter asymptotic bound in the average case.
------------------------------------------------------
Memory:
 All cases: O(1)
======================================================
Unsorted: Table Lookup

Time:
 Key operation : table.containsKey(curNum)
 Best case     : O(1) as only two checks will need to be performed.
 Worst case    : O(n) as every single value in the array will need to be checked in the hash map exactly once.
 Average case  : The average # of values that have to be checked before one that is in a valid solution is found is in O(n),
                 but each of these only needs to be checked against the hash map exactly once.
				 A(n) = O(n)
------------------------------------------------------
Memory:
 Best case   : O(1) as only the first value needs to be stored in the hash table
 Worst case  : O(1) as the maximum number of entries in the hash table is a constant (the # of possible integer values, 201 in this case).
 Average case: O(1) follows from Best/Worst case both being in O(1). 
======================================================
Sorted: Brute Force w/ MinMax

Time:
 Key operation : Comparison (num1 + num2 == sum)

 The # of key operations performed is equal to or less than those in the previous examples. Hence the upper bounds still hold:
 B(n) = O(1)
 W(n) = O(n^2)
 A(n) = O(n^2)
 
I suspect it can be shown that the reduced # of operations results in a slightly tighter asymptotic bound in the average case.

------------------------------------------------------
Memory:
 All cases: O(1)
======================================================
Sorted: Pincer Search

Time:
 Key operation : Comparison (num1 + num2 == sum)
 Best case     : O(1) as the very first two values checked will form a solution.
 Worst case    : O(n) as the solution is not in the array & hence (n-1) comparisons will need to be made
 Average case  : The average # of values that have to be checked before one that is in a valid solution is found is in O(n),
                 but each time a comparison is made a value is eliminated from consideration.
				 Hence the total # of checks is also in O(n)
				 A(n) = O(n).
------------------------------------------------------
Memory:
 All cases: O(1)
======================================================
*/

/*
Now solving for input size n = 100
UNSORTED:
Brute solution: avg time is 7134ns
     w/ MinMax: avg time is 1379ns
Table solution: avg time is 2414ns
SORTED:
Brute w/MinMax: avg time is 870ns
        Pincer: avg time is 440ns

Now solving for input size n = 1000
UNSORTED:
Brute solution: avg time is 3481ns
     w/ MinMax: avg time is 207ns
Table solution: avg time is 1266ns
SORTED:
Brute w/MinMax: avg time is 635ns
        Pincer: avg time is 227ns

Now solving for input size n = 100000
UNSORTED:
Brute solution: avg time is 256774ns
     w/ MinMax: avg time is 224ns
Table solution: avg time is 1322ns
SORTED:
Brute w/MinMax: avg time is 54718ns
        Pincer: avg time is 862ns

Now solving for input size n = 1000000
UNSORTED:
Brute solution: avg time is 3246852ns
     w/ MinMax: avg time is 398ns
Table solution: avg time is 1988ns
SORTED:
Brute w/MinMax: avg time is 629792ns
        Pincer: avg time is 7085ns
*/

import java.util.*;
import java.util.Random;
import java.util.concurrent.TimeUnit;

class FindSum  
{ 
	public static final int[] SIZES = {100,1000,100000,1000000};
	public static int ITERATIONS = 10000;
	public static int INT_MINIMUM = -100;
	public static int INT_MAXIMUM = 100;
	
	/*
	Brute force solution for finding indices of two addends, assuming that the input array is not sorted.
	Returns an int array of size 2 consisting of the two indices, or {-1,-1} if no solution exists.
	*/
	public static int[] bruteForce_unsorted(int[] arr, int sum){
		int n = arr.length;
		for (int i=0; i<n-1; i++)
		{
			int curNum = arr[i];
			for (int j=i+1; j<n; j++)
				if (arr[j] + curNum == sum)
					return new int[] {i,j};
		}
		return new int[] {-1,-1};
	}
	// end bruteForce_unsorted
	
	/*
	The above brute force solution modified such that it skips checking any addends that cannot
	 possibly add with any other addend to get the target sum, based on the known ranges of
	 -100 to 100 for the addends and -200 to 200 for the sum.
	Returns an int array of size 2 consisting of the two indices, or {-1,-1} if no solution exists.
	*/
	public static int[] bruteForce_minMax_unsorted(int[] arr, int sum){
		int n = arr.length;
		int minAdd = Math.max(INT_MINIMUM, sum + INT_MINIMUM);
		int maxAdd = Math.min(INT_MAXIMUM, sum + INT_MAXIMUM);
		for (int i=0; i<n-1; i++)
		{
			int curNum = arr[i];
			if (curNum >= minAdd && curNum <= maxAdd)
				for (int j=i+1; j<n; j++)
					if (arr[j] + curNum == sum)
						return new int[] {i,j};
		}
		return new int[] {-1,-1};
	}
	// end bruteForce_minMax_unsorted
	
	/*
	Uses a hashmap to store the index of each value in the array until a match
	 for the complement (the number to add to it to get the target sum) is found.
	Returns an int array of size 2 consisting of the two indices, or {-1,-1} if no solution exists.
	*/
	public static int[] lookupTable_unsorted(int[] arr, int sum){
		int n = arr.length;
		HashMap<Integer, Integer> table = new HashMap<Integer,Integer>();
		int minAdd = Math.max(INT_MINIMUM, sum + INT_MINIMUM);
		int maxAdd = Math.min(INT_MAXIMUM, sum + INT_MAXIMUM);

		for (int i=0; i<n; i++)
		{
			int curNum = arr[i];
			if (curNum >= minAdd && curNum <= maxAdd)
				if (table.containsKey(curNum))
					return new int[] {table.get(curNum), i};
				else
					table.put(sum-curNum, i);
		}
		return new int[] {-1,-1};
	}	
	// end lookupTable_unsorted

	/*
	Brute force solution for finding indices of two addends, assuming that the input array is sorted.
	 Note that sorting the array causes the regular brute force method to take much longer than if the
	 array is sorted so in this case I used the minmax method to reduce the # of comparisons and
	 have this finish faster.
	Returns an int array of size 2 consisting of the two indices, or {-1,-1} if no solution exists.
	*/
	public static int[] bruteForce_minMax_sorted(int[] arr, int sum){
		int n = arr.length;
		int minAdd = Math.max(INT_MINIMUM, sum + INT_MINIMUM);
		int maxAdd = Math.min(INT_MAXIMUM, sum + INT_MAXIMUM);
		for (int i=0; i<n-1; i++)
		{
			int curNum = arr[i];
			if (curNum >= minAdd && curNum <= maxAdd)
				for (int j=i+1; j<n; j++)
					if (arr[j] + curNum == sum)
						return new int[] {i,j};
		}
		return new int[] {-1,-1};
	}
	// end bruteForce_minMax_sorted

	/*
	Takes advantages of the array being sorted & looks for the target sum by searching from both the 
	 low and high end of the array at once and narrowing down the search.
	Returns an int array of size 2 consisting of the two indices, or {-1,-1} if no solution exists.
	*/	
	public static int[] pincer_sorted(int[] arr, int sum){
		int minAdd = Math.max(INT_MINIMUM, sum + INT_MINIMUM);
		int maxAdd = Math.min(INT_MAXIMUM, sum + INT_MAXIMUM);	
		
		// Skip over the sections of the array that contain addends that cannot be part of a valid solution.
		
		// Rather than iterating through the array from the start/end,
		//  use binary search to find the indices just before/after all instances of minAdd/maxAdd
		int minIdx = Arrays.binarySearch(arr,minAdd-1);
		int maxIdx = Arrays.binarySearch(arr,maxAdd+1);
		// If not in the array, derive the correct index position from the negative value returned
		if (minIdx < 0)
			minIdx = Math.max(-1*minIdx - 1, 0);
		if (maxIdx < 0)
			maxIdx = Math.min(-1*maxIdx - 2, arr.length - 1);
		
		while (minIdx < maxIdx) {
			int curSum = arr[minIdx] + arr[maxIdx];
			if (curSum == sum)
				return new int[] {minIdx, maxIdx};
			// Iterate through duplicate entries in array until the next largest/smallest value is found
			else if (curSum < sum)
				do { minIdx++; } while (minIdx < maxIdx && arr[minIdx] == arr[minIdx-1]);
			else
				do { maxIdx--; } while (minIdx < maxIdx && arr[maxIdx] == arr[maxIdx+1]);
		}
		return new int[] {-1,-1};
	}
	// end pincer_sorted
	
	/*
	Prints an integer array to console.
	Used for debug.
	*/
	public static void printArray(int[] arr){
		for (int i=0; i<arr.length; i++)
			System.out.print(arr[i] + " ");
		System.out.println();		
	}
	// end printArray

    public static void main(String[] args)  
    { 
		Random rand = new Random();
		for (int i=0; i<SIZES.length; i++)
		{
			int curSize = SIZES[i];
			System.out.println("Now solving for input size n = " + curSize);
			long totalTime_brute_unsorted = 0;
			long totalTime_brute_minmax_unsorted = 0;
			long totalTime_smart_unsorted = 0;
			
			long totalTime_brute_minmax_sorted = 0;
			long totalTime_pincer_sorted = 0;
			for (int j=0; j<ITERATIONS; j++)
			{
				int[] arr = new int[curSize];
				for (int k=0; k<curSize; k++)
					arr[k] = rand.nextInt(201) - 100;
				int sum = rand.nextInt(401) - 200;
				
				long startTime, endTime;
				int[] solution1;
				int[] solution2;
				int[] solution3;
				
				int[] solution4;
				int[] solution5;
				
				startTime = System.nanoTime();
				solution1 = bruteForce_unsorted(arr, sum);
				endTime = System.nanoTime();
				totalTime_brute_unsorted += (endTime - startTime);
				
				startTime = System.nanoTime();
				solution2 = bruteForce_minMax_unsorted(arr, sum);
				endTime = System.nanoTime();
				totalTime_brute_minmax_unsorted += (endTime - startTime);
				
				startTime = System.nanoTime();
				solution3 = lookupTable_unsorted(arr, sum);
				endTime = System.nanoTime();
				totalTime_smart_unsorted += (endTime - startTime);
				
				if (solution1[0] != -1 || solution2[0] != -1 || solution3[0] != -1)
				{
					int sum1 = arr[solution1[0]] + arr[solution1[1]];
					int sum2 = arr[solution2[0]] + arr[solution2[1]];
					int sum3 = arr[solution3[0]] + arr[solution3[1]];
					
					if (sum1 != sum2 || sum2 != sum3)
					{
						printArray(solution1);
						printArray(solution2);
						printArray(solution3);
						throw new ArithmeticException("One or more of the algorithms gave an invalid solution");
					}					
				}
				
				Arrays.sort(arr);
				
				startTime = System.nanoTime();
				solution4 = bruteForce_minMax_sorted(arr, sum);
				endTime = System.nanoTime();
				totalTime_brute_minmax_sorted += (endTime - startTime);			

				startTime = System.nanoTime();
				solution5 = pincer_sorted(arr, sum);
				endTime = System.nanoTime();
				totalTime_pincer_sorted += (endTime - startTime);					

				if (solution4[0] != -1 || solution5[0] != -1)
				{
					int sum4 = arr[solution4[0]] + arr[solution4[1]];
					int sum5 = arr[solution5[0]] + arr[solution5[1]];
					
					if (sum4 != sum5)
					{
						printArray(solution4);
						printArray(solution5);
						throw new ArithmeticException("One or more of the algorithms gave an invalid solution");
					}					
				}
				
			}
			System.out.println("UNSORTED:");
			System.out.println("Brute solution: avg time is " + (totalTime_brute_unsorted/ITERATIONS) + "ns");
			System.out.println("     w/ MinMax: avg time is " + (totalTime_brute_minmax_unsorted/ITERATIONS) + "ns");
			System.out.println("Table solution: avg time is " + (totalTime_smart_unsorted/ITERATIONS) + "ns");
			System.out.println("SORTED:");
			System.out.println("Brute w/MinMax: avg time is " + (totalTime_brute_minmax_sorted/ITERATIONS) + "ns");
			System.out.println("        Pincer: avg time is " + (totalTime_pincer_sorted/ITERATIONS) + "ns");
			System.out.println();
		}
    } 
	// end main
} 
