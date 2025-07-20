# Sorting Algorithm Visualizer

A Java Swing application that provides a visual representation of various sorting algorithms in action.

## Features

- Real-time visualization of 6 sorting algorithms:
  - Bubble Sort
  - Selection Sort
  - Insertion Sort
  - Merge Sort
  - Quick Sort
  - Heap Sort
- Interactive controls for:
  - Algorithm selection
  - Animation speed adjustment
  - Array size modification
- Color-coded visualization:
  - Blue: Unsorted elements
  - Red: Elements being compared
  - Green: Sorted elements
  - Purple: Pivot element (for applicable algorithms)
- Performance metrics and complexity information
- Modern and responsive UI design

## Requirements

- Java Runtime Environment (JRE) 8 or higher
- Java Development Kit (JDK) for compilation

## How to Run

1. Compile the Java file:
```bash
javac SortingVisualizerComplete.java
```

2. Run the compiled program:
```bash
java SortingVisualizerComplete
```

## Usage

1. Select a sorting algorithm from the dropdown menu
2. Adjust the animation speed using the speed slider
3. Set the array size using the size slider
4. Click "Start Sorting" to begin the visualization
5. Use "Reset Array" to generate a new random array
6. Use "Shuffle Array" to randomize the current array

## Algorithm Complexities

- Bubble Sort: O(n²) time, O(1) space
- Selection Sort: O(n²) time, O(1) space
- Insertion Sort: O(n²) time, O(1) space
- Merge Sort: O(n log n) time, O(n) space
- Quick Sort: O(n log n) average time, O(log n) space
- Heap Sort: O(n log n) time, O(1) space