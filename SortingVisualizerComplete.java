import javax.swing.*;
import javax.swing.plaf.basic.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class SortingVisualizerComplete extends JFrame {
    

    interface SortingAlgorithm {
        void sort(int[] array, VisualizationPanel panel);
    }
    

    class VisualizationPanel extends JPanel {
        private int[] array;
        private int arraySize = 100;
        private int delay = 50;
        private boolean sorting = false;
        private int comparing1 = -1;
        private int comparing2 = -1;
        private int sorted = -1;
        private int pivot = -1;
        
        private final Color DEFAULT_COLOR = new Color(52, 152, 219);
        private final Color COMPARING_COLOR = new Color(231, 76, 60);
        private final Color SORTED_COLOR = new Color(39, 174, 96);
        private final Color PIVOT_COLOR = new Color(155, 89, 182);
        
        public VisualizationPanel() {
            setBackground(Color.WHITE);
            setPreferredSize(new Dimension(800, 600));
            initializeArray();
        }
        
        private void initializeArray() {
            array = new int[arraySize];
            shuffleArray();
        }
        
        public void shuffleArray() {
            Random random = new Random();
            int maxHeight = Math.max(400, getHeight() - 100);
            for (int i = 0; i < arraySize; i++) {
                array[i] = random.nextInt(maxHeight - 10) + 10;
            }
            resetVisualizationState();
            repaint();
        }
        
        private void resetVisualizationState() {
            comparing1 = -1;
            comparing2 = -1;
            sorted = -1;
            pivot = -1;
        }
        
        public void setArraySize(int size) {
            this.arraySize = size;
            initializeArray();
        }
        
        public void setDelay(int delay) {
            this.delay = Math.max(1, delay);
        }
        
        public boolean isSorting() {
            return sorting;
        }
        
        public void stopSorting() {
            sorting = false;
            resetVisualizationState();
            repaint();
        }
        
        public void startSorting(SortingAlgorithm algorithm, Runnable onComplete) {
            sorting = true;
            resetVisualizationState();
            
            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    algorithm.sort(array, VisualizationPanel.this);
                    return null;
                }
                
                @Override
                protected void done() {
                    sorting = false;
                        for (int i = 0; i < arraySize && sorting == false; i++) {
                        sorted = i;
                        SwingUtilities.invokeLater(() -> repaint());
                        try {
                            Thread.sleep(Math.max(1, delay / 3));
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }
                    resetVisualizationState();
                    SwingUtilities.invokeLater(() -> repaint());
                    if (onComplete != null) {
                        SwingUtilities.invokeLater(onComplete);
                    }
                }
            };
            
            worker.execute();
        }
        
        public void updateVisualization(int index1, int index2) {
            updateVisualization(index1, index2, -1, -1);
        }
        
        public void updateVisualization(int index1, int index2, int sortedIndex, int pivotIndex) {
            if (!sorting) return;
            
            comparing1 = index1;
            comparing2 = index2;
            sorted = sortedIndex;
            pivot = pivotIndex;
            
            SwingUtilities.invokeLater(() -> repaint());
            
            try {
                Thread.sleep(Math.max(1, delay));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        public void markSorted(int index) {
            if (!sorting) return;
            
            sorted = index;
            SwingUtilities.invokeLater(() -> repaint());
            try {
                Thread.sleep(Math.max(1, delay / 2));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        public void swap(int i, int j) {
            if (i >= 0 && j >= 0 && i < array.length && j < array.length) {
                int temp = array[i];
                array[i] = array[j];
                array[j] = temp;
            }
        }
        
        public int[] getArray() {
            return array;
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int panelWidth = getWidth();
            int panelHeight = getHeight();
            
            if (array == null || arraySize == 0 || panelWidth <= 0 || panelHeight <= 50) return;
            
            double barWidth = (double) panelWidth / arraySize;
            double maxHeight = panelHeight - 50;
            int maxValue = getMaxValue();
            
            if (maxValue == 0) return;
            
            for (int i = 0; i < arraySize; i++) {
                double barHeight = (double) array[i] / maxValue * maxHeight;
                int x = (int) (i * barWidth);
                int y = (int) (panelHeight - barHeight - 25);
                int width = Math.max(1, (int) barWidth - 1);
                int height = (int) barHeight;
                
                Color barColor = DEFAULT_COLOR;
                
                if (i == pivot) {
                    barColor = PIVOT_COLOR;
                } else if (i == comparing1 || i == comparing2) {
                    barColor = COMPARING_COLOR;
                } else if (i <= sorted) {
                    barColor = SORTED_COLOR;
                }
                
                g2d.setColor(barColor);
                g2d.fillRect(x, y, width, height);
                
                g2d.setColor(barColor.darker());
                g2d.drawRect(x, y, width, height);
                
                if (arraySize <= 50) {
                    g2d.setColor(Color.BLACK);
                    g2d.setFont(new Font("Arial", Font.PLAIN, 10));
                    String value = String.valueOf(array[i]);
                    FontMetrics fm = g2d.getFontMetrics();
                    int textX = x + width/2 - fm.stringWidth(value)/2;
                    int textY = Math.max(15, y - 5);
                    g2d.drawString(value, textX, textY);
                }
            }
            
            drawLegend(g2d, panelWidth, panelHeight);
        }
        
        private void drawLegend(Graphics2D g2d, int panelWidth, int panelHeight) {
            int legendY = panelHeight - 20;
            int legendX = 20;
            int boxSize = 15;
            int spacing = 120;
            
            g2d.setFont(new Font("Arial", Font.BOLD, 12));
            
            g2d.setColor(DEFAULT_COLOR);
            g2d.fillRect(legendX, legendY, boxSize, boxSize);
            g2d.setColor(Color.BLACK);
            g2d.drawString("Unsorted", legendX + boxSize + 5, legendY + 12);
            
            legendX += spacing;
            g2d.setColor(COMPARING_COLOR);
            g2d.fillRect(legendX, legendY, boxSize, boxSize);
            g2d.setColor(Color.BLACK);
            g2d.drawString("Comparing", legendX + boxSize + 5, legendY + 12);
            
            legendX += spacing;
            g2d.setColor(SORTED_COLOR);
            g2d.fillRect(legendX, legendY, boxSize, boxSize);
            g2d.setColor(Color.BLACK);
            g2d.drawString("Sorted", legendX + boxSize + 5, legendY + 12);
            
            if (legendX + spacing + 100 < panelWidth) {
                legendX += spacing;
                g2d.setColor(PIVOT_COLOR);
                g2d.fillRect(legendX, legendY, boxSize, boxSize);
                g2d.setColor(Color.BLACK);
                g2d.drawString("Pivot", legendX + boxSize + 5, legendY + 12);
            }
        }
        
        private int getMaxValue() {
            int max = 1;            for (int value : array) {
                max = Math.max(max, value);
            }
            return max;
        }
    }
    
    class BubbleSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, VisualizationPanel panel) {
            int n = array.length;
            
            for (int i = 0; i < n - 1 && panel.isSorting(); i++) {
                boolean swapped = false;
                
                for (int j = 0; j < n - i - 1 && panel.isSorting(); j++) {
                    panel.updateVisualization(j, j + 1, n - i - 1, -1);
                    
                    if (array[j] > array[j + 1]) {
                        panel.swap(j, j + 1);
                        swapped = true;
                        panel.updateVisualization(j, j + 1, n - i - 1, -1);
                    }
                }
                
                panel.markSorted(n - i - 1);
                if (!swapped) break;
            }
            
            if (panel.isSorting()) {
                panel.markSorted(0);
            }
        }
    }
    
    class SelectionSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, VisualizationPanel panel) {
            int n = array.length;
            
            for (int i = 0; i < n - 1 && panel.isSorting(); i++) {
                int minIndex = i;
                
                for (int j = i + 1; j < n && panel.isSorting(); j++) {
                    panel.updateVisualization(minIndex, j, i - 1, -1);
                    
                    if (array[j] < array[minIndex]) {
                        minIndex = j;
                    }
                }
                
                if (minIndex != i && panel.isSorting()) {
                    panel.updateVisualization(i, minIndex, i - 1, -1);
                    panel.swap(i, minIndex);
                }
                
                panel.markSorted(i);
            }
            
            if (panel.isSorting()) {
                panel.markSorted(n - 1);
            }
        }
    }
    
    class InsertionSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, VisualizationPanel panel) {
            int n = array.length;
            
            for (int i = 1; i < n && panel.isSorting(); i++) {
                int key = array[i];
                int j = i - 1;
                
                panel.updateVisualization(i, -1, -1, -1);
                
                while (j >= 0 && array[j] > key && panel.isSorting()) {
                    panel.updateVisualization(j, j + 1, -1, -1);
                    array[j + 1] = array[j];
                    j--;
                    
                    if (j >= 0) {
                        panel.updateVisualization(j + 1, j + 2, -1, -1);
                    }
                }
                
                if (panel.isSorting()) {
                    array[j + 1] = key;
                    panel.updateVisualization(j + 1, -1, i, -1);
                }
            }
        }
    }
    
    class MergeSort implements SortingAlgorithm {
        private VisualizationPanel panel;
        
        @Override
        public void sort(int[] array, VisualizationPanel panel) {
            this.panel = panel;
            mergeSort(array, 0, array.length - 1);
        }
        
        private void mergeSort(int[] array, int left, int right) {
            if (left < right && panel.isSorting()) {
                int middle = left + (right - left) / 2;
                panel.updateVisualization(left, right, -1, middle);
                
                mergeSort(array, left, middle);
                mergeSort(array, middle + 1, right);
                merge(array, left, middle, right);
            }
        }
        
        private void merge(int[] array, int left, int middle, int right) {
            if (!panel.isSorting()) return;
            
            int[] leftArray = new int[middle - left + 1];
            int[] rightArray = new int[right - middle];
            
            for (int i = 0; i < leftArray.length; i++) {
                leftArray[i] = array[left + i];
            }
            for (int j = 0; j < rightArray.length; j++) {
                rightArray[j] = array[middle + 1 + j];
            }
            
            int i = 0, j = 0, k = left;
            
            while (i < leftArray.length && j < rightArray.length && panel.isSorting()) {
                panel.updateVisualization(left + i, middle + 1 + j, k - 1, -1);
                
                if (leftArray[i] <= rightArray[j]) {
                    array[k] = leftArray[i];
                    i++;
                } else {
                    array[k] = rightArray[j];
                    j++;
                }
                k++;
                panel.updateVisualization(k - 1, -1, k - 1, -1);
            }
            
            while (i < leftArray.length && panel.isSorting()) {
                array[k] = leftArray[i];
                panel.updateVisualization(k, -1, k, -1);
                i++;
                k++;
            }
            
            while (j < rightArray.length && panel.isSorting()) {
                array[k] = rightArray[j];
                panel.updateVisualization(k, -1, k, -1);
                j++;
                k++;
            }
        }
    }
    
    class QuickSort implements SortingAlgorithm {
        private VisualizationPanel panel;
        
        @Override
        public void sort(int[] array, VisualizationPanel panel) {
            this.panel = panel;
            quickSort(array, 0, array.length - 1);
        }
        
        private void quickSort(int[] array, int low, int high) {
            if (low < high && panel.isSorting()) {
                int pivotIndex = partition(array, low, high);
                
                if (panel.isSorting()) {
                    panel.updateVisualization(-1, -1, pivotIndex, pivotIndex);
                }
                
                quickSort(array, low, pivotIndex - 1);
                quickSort(array, pivotIndex + 1, high);
            }
        }
        
        private int partition(int[] array, int low, int high) {
            int pivot = array[high];
            int i = low - 1;
            
            for (int j = low; j < high && panel.isSorting(); j++) {
                panel.updateVisualization(j, high, -1, high);
                
                if (array[j] <= pivot) {
                    i++;
                    panel.swap(i, j);
                    panel.updateVisualization(i, j, -1, high);
                }
            }
            
            if (panel.isSorting()) {
                panel.swap(i + 1, high);
                panel.updateVisualization(i + 1, high, -1, i + 1);
            }
            
            return i + 1;
        }
    }
    
    class HeapSort implements SortingAlgorithm {
        private VisualizationPanel panel;
        
        @Override
        public void sort(int[] array, VisualizationPanel panel) {
            this.panel = panel;
            int n = array.length;
            
            for (int i = n / 2 - 1; i >= 0 && panel.isSorting(); i--) {
                heapify(array, n, i);
            }
            
            for (int i = n - 1; i > 0 && panel.isSorting(); i--) {
                panel.updateVisualization(0, i, i, 0);
                panel.swap(0, i);
                panel.markSorted(i);
                heapify(array, i, 0);
            }
            
            if (panel.isSorting()) {
                panel.markSorted(0);
            }
        }
        
        private void heapify(int[] array, int n, int root) {
            if (!panel.isSorting()) return;
            
            int largest = root;
            int left = 2 * root + 1;
            int right = 2 * root + 2;
            
            panel.updateVisualization(root, -1, -1, root);
            
            if (left < n && panel.isSorting()) {
                panel.updateVisualization(left, largest, -1, root);
                if (array[left] > array[largest]) {
                    largest = left;
                }
            }
            
            if (right < n && panel.isSorting()) {
                panel.updateVisualization(right, largest, -1, root);
                if (array[right] > array[largest]) {
                    largest = right;
                }
            }
            
            if (largest != root && panel.isSorting()) {
                panel.updateVisualization(root, largest, -1, root);
                panel.swap(root, largest);
                heapify(array, n, largest);
            }
        }
    }
    

    private VisualizationPanel visualizationPanel;
    private JComboBox<String> algorithmSelector;
    private JSlider speedSlider;
    private JSlider sizeSlider;
    private JButton startButton;
    private JButton resetButton;
    private JButton shuffleButton;
    private JLabel speedLabel;
    private JLabel sizeLabel;
    private JLabel statusLabel;
    private JLabel complexityLabel;
    
    private BubbleSort bubbleSort;
    private SelectionSort selectionSort;
    private InsertionSort insertionSort;
    private MergeSort mergeSort;
    private QuickSort quickSort;
    private HeapSort heapSort;
    
    public SortingVisualizerComplete() {
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        
        // Initialize sorting algorithms
        bubbleSort = new BubbleSort();
        selectionSort = new SelectionSort();
        insertionSort = new InsertionSort();
        mergeSort = new MergeSort();
        quickSort = new QuickSort();
        heapSort = new HeapSort();
        
        setTitle("🎯 Sorting Algorithm Visualizer - Complete DSA Project");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        
        // Initialize with random data after a short delay
        SwingUtilities.invokeLater(() -> {
            shuffleArray();
            updateComplexityInfo();
        });
    }
    
    private void initializeComponents() {
        // Main visualization panel
        visualizationPanel = new VisualizationPanel();
        
        // Algorithm selector
        String[] algorithms = {
            "Bubble Sort O(n²)", "Selection Sort O(n²)", "Insertion Sort O(n²)", 
            "Merge Sort O(n log n)", "Quick Sort O(n log n)", "Heap Sort O(n log n)"
        };
        algorithmSelector = new JComboBox<>(algorithms);
        algorithmSelector.setFont(new Font("Arial", Font.BOLD, 14));
        algorithmSelector.setBackground(new Color(255, 255, 255));
        algorithmSelector.setForeground(new Color(44, 62, 80));
        algorithmSelector.setBorder(BorderFactory.createLineBorder(new Color(52, 152, 219), 2));
        ((JComponent) algorithmSelector.getRenderer()).setOpaque(true);
        
        // Style the ComboBox popup menu
        algorithmSelector.setUI(new BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton button = super.createArrowButton();
                button.setBackground(new Color(52, 152, 219));
                button.setBorder(BorderFactory.createLineBorder(new Color(52, 152, 219), 1));
                return button;
            }
        });
        
        // Add hover effect to ComboBox
        algorithmSelector.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                algorithmSelector.setBorder(BorderFactory.createLineBorder(new Color(52, 152, 219).brighter(), 2));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                algorithmSelector.setBorder(BorderFactory.createLineBorder(new Color(52, 152, 219), 2));
            }
        });
        
        // Speed control
        speedSlider = new JSlider(1, 100, 50);
        speedSlider.setPaintTicks(true);
        speedSlider.setPaintLabels(true);
        speedSlider.setMajorTickSpacing(25);
        speedSlider.setBackground(new Color(236, 240, 241));
        speedSlider.setForeground(new Color(44, 62, 80));
        speedSlider.setFont(new Font("Arial", Font.PLAIN, 12));
        speedLabel = new JLabel("⚡ Animation Speed: 50");
        speedLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        // Style the slider
        speedSlider.setUI(new BasicSliderUI(speedSlider) {
            @Override
            public void paintThumb(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(52, 152, 219));
                g2d.fillOval(thumbRect.x, thumbRect.y, thumbRect.width, thumbRect.height);
                g2d.setColor(new Color(52, 152, 219).darker());
                g2d.drawOval(thumbRect.x, thumbRect.y, thumbRect.width, thumbRect.height);
            }
        });
        
        // Array size control
        sizeSlider = new JSlider(10, 300, 100);
        sizeSlider.setPaintTicks(true);
        sizeSlider.setPaintLabels(true);
        sizeSlider.setMajorTickSpacing(50);
        sizeSlider.setBackground(new Color(236, 240, 241));
        sizeSlider.setForeground(new Color(44, 62, 80));
        sizeSlider.setFont(new Font("Arial", Font.PLAIN, 12));
        sizeLabel = new JLabel("📊 Array Size: 100");
        sizeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        // Style the slider
        sizeSlider.setUI(new BasicSliderUI(sizeSlider) {
            @Override
            public void paintThumb(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(52, 152, 219));
                g2d.fillOval(thumbRect.x, thumbRect.y, thumbRect.width, thumbRect.height);
                g2d.setColor(new Color(52, 152, 219).darker());
                g2d.drawOval(thumbRect.x, thumbRect.y, thumbRect.width, thumbRect.height);
            }
        });
        
        // Control buttons
        startButton = new JButton("🚀 Start Sorting");
        resetButton = new JButton("🔄 Reset Array");
        shuffleButton = new JButton("🎲 Shuffle Array");
        
        // Style buttons
        styleButton(startButton, new Color(39, 174, 96));
        styleButton(resetButton, new Color(231, 76, 60));
        styleButton(shuffleButton, new Color(52, 152, 219));
        
        // Status and info labels
        statusLabel = new JLabel("🎯 Ready to sort! Select algorithm and click Start.");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
        statusLabel.setForeground(new Color(44, 62, 80));
        
        complexityLabel = new JLabel("Time: O(n²) | Space: O(1) | Stable: Yes");
        complexityLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        complexityLabel.setForeground(new Color(127, 140, 141));
    }
    
    private void styleButton(JButton button, Color color) {
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setFocusPainted(false);
        button.setBorderPainted(true);
        button.setBorder(BorderFactory.createLineBorder(color.darker(), 2));
        button.setPreferredSize(new Dimension(180, 45));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        
        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.brighter());
                button.setBorder(BorderFactory.createLineBorder(color, 2));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
                button.setBorder(BorderFactory.createLineBorder(color.darker(), 2));
            }
        });
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Top panel for title
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(44, 62, 80));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("🎯 Sorting Algorithm Visualizer - Data Structures & Algorithms");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        
        // Control panel with GridBagLayout for better responsiveness
        JPanel controlPanel = new JPanel(new GridBagLayout());
        controlPanel.setBackground(new Color(236, 240, 241));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        
        // Add components to control panel with GridBagLayout
        JLabel algoLabel = new JLabel("🔧 Algorithm:");
        algoLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        // Algorithm selector panel
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        controlPanel.add(algoLabel, gbc);
        gbc.gridy = 1;
        algorithmSelector.setPreferredSize(new Dimension(250, 40));
        controlPanel.add(algorithmSelector, gbc);
        
        // Speed control panel
        gbc.gridx = 2; gbc.gridy = 0; gbc.gridwidth = 2;
        speedLabel.setFont(new Font("Arial", Font.BOLD, 14));
        controlPanel.add(speedLabel, gbc);
        gbc.gridy = 1;
        speedSlider.setPreferredSize(new Dimension(200, 40));
        controlPanel.add(speedSlider, gbc);
        
        // Size control panel
        gbc.gridx = 4; gbc.gridy = 0; gbc.gridwidth = 2;
        sizeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        controlPanel.add(sizeLabel, gbc);
        gbc.gridy = 1;
        sizeSlider.setPreferredSize(new Dimension(200, 40));
        controlPanel.add(sizeSlider, gbc);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setBackground(controlPanel.getBackground());
        buttonPanel.add(startButton);
        buttonPanel.add(resetButton);
        buttonPanel.add(shuffleButton);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 6;
        controlPanel.add(buttonPanel, gbc);
        
        // Info panel
        JPanel infoPanel = new JPanel(new FlowLayout());
        infoPanel.setBackground(new Color(249, 249, 249));
        infoPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 2),
            "📊 Algorithm Information",
            0, 0, new Font("Arial", Font.BOLD, 14), new Color(44, 62, 80)
        ));
        infoPanel.add(complexityLabel);
        
        // Combined control panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(titlePanel, BorderLayout.NORTH);
        topPanel.add(controlPanel, BorderLayout.CENTER);
        topPanel.add(infoPanel, BorderLayout.SOUTH);
        
        // Status panel
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(new Color(52, 73, 94));
        statusPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        statusLabel.setForeground(Color.WHITE);
        statusPanel.add(statusLabel, BorderLayout.CENTER);
        
        // Add panels to frame
        add(topPanel, BorderLayout.NORTH);
        add(visualizationPanel, BorderLayout.CENTER);
        add(statusPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventHandlers() {
        // Algorithm selection
        algorithmSelector.addActionListener(e -> updateComplexityInfo());
        
        // Speed slider
        speedSlider.addChangeListener(e -> {
            int speed = speedSlider.getValue();
            speedLabel.setText("⚡ Animation Speed: " + speed);
            visualizationPanel.setDelay(101 - speed);
        });
        
        // Size slider
        sizeSlider.addChangeListener(e -> {
            int size = sizeSlider.getValue();
            sizeLabel.setText("📊 Array Size: " + size);
            if (!visualizationPanel.isSorting()) {
                visualizationPanel.setArraySize(size);
                shuffleArray();
            }
        });
        
        // Start button
        startButton.addActionListener(e -> startSorting());
        
        // Reset button
        resetButton.addActionListener(e -> {
            visualizationPanel.stopSorting();
            shuffleArray();
            startButton.setText("🚀 Start Sorting");
            startButton.setEnabled(true);
            statusLabel.setText("🔄 Array reset and shuffled!");
        });
        
        // Shuffle button
        shuffleButton.addActionListener(e -> {
            if (!visualizationPanel.isSorting()) {
                shuffleArray();
                statusLabel.setText("🎲 Array shuffled randomly!");
            }
        });
    }
    
    private void startSorting() {
        String selectedAlgorithm = (String) algorithmSelector.getSelectedItem();
        if (selectedAlgorithm == null) return;
        
        String algorithmName = selectedAlgorithm.split(" O\\(")[0]; // Extract algorithm name
        
        startButton.setText("⏸️ Sorting...");
        startButton.setEnabled(false);
        statusLabel.setText("🚀 Sorting with " + algorithmName + "... Watch the magic happen!");
        
        SortingAlgorithm algorithm;
        switch (algorithmName) {
            case "Bubble Sort":
                algorithm = bubbleSort;
                break;
            case "Selection Sort":
                algorithm = selectionSort;
                break;
            case "Insertion Sort":
                algorithm = insertionSort;
                break;
            case "Merge Sort":
                algorithm = mergeSort;
                break;
            case "Quick Sort":
                algorithm = quickSort;
                break;
            case "Heap Sort":
                algorithm = heapSort;
                break;
            default:
                algorithm = bubbleSort;
        }
        
        visualizationPanel.startSorting(algorithm, () -> {
            startButton.setText("🚀 Start Sorting");
            startButton.setEnabled(true);
            statusLabel.setText("✅ Sorting completed successfully! Array is now sorted.");
        });
    }
    
    private void shuffleArray() {
        visualizationPanel.shuffleArray();
    }
    
    private void updateComplexityInfo() {
        String selectedAlgorithm = (String) algorithmSelector.getSelectedItem();
        if (selectedAlgorithm == null) return;
        
        String algorithmName = selectedAlgorithm.split(" O\\(")[0];
        String complexity = "";
        
        switch (algorithmName) {
            case "Bubble Sort":
                complexity = "⏱️ Time: O(n²) | 💾 Space: O(1) | 🔄 Stable: Yes | 📈 Best for: Educational purposes";
                break;
            case "Selection Sort":
                complexity = "⏱️ Time: O(n²) | 💾 Space: O(1) | 🔄 Stable: No | 📈 Best for: Small datasets";
                break;
            case "Insertion Sort":
                complexity = "⏱️ Time: O(n²) | 💾 Space: O(1) | 🔄 Stable: Yes | 📈 Best for: Nearly sorted data";
                break;
            case "Merge Sort":
                complexity = "⏱️ Time: O(n log n) | 💾 Space: O(n) | 🔄 Stable: Yes | 📈 Best for: Large datasets";
                break;
            case "Quick Sort":
                complexity = "⏱️ Time: O(n log n) avg | 💾 Space: O(log n) | 🔄 Stable: No | 📈 Best for: General purpose";
                break;
            case "Heap Sort":
                complexity = "⏱️ Time: O(n log n) | 💾 Space: O(1) | 🔄 Stable: No | 📈 Best for: Guaranteed performance";
                break;
        }
        
        complexityLabel.setText(complexity);
    }
    
    // ================================================================================================
    // MAIN METHOD - COMPLETELY FIXED FOR ALL JAVA VERSIONS
    // ================================================================================================
    
    public static void main(String[] args) {
        // Simple look and feel setup that works on all Java versions
        try {
            // Just use the default Swing look and feel - no special methods needed
            System.out.println("✅ Using default Swing Look and Feel");
        } catch (Exception e) {
            System.out.println("⚠️ Using basic Look and Feel");
        }
        
        // Create and show the application
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                System.out.println("🎯 Starting Sorting Algorithm Visualizer...");
                System.out.println("📚 Algorithms included:");
                System.out.println("   🫧 Bubble Sort - O(n²) time, O(1) space");
                System.out.println("   🎯 Selection Sort - O(n²) time, O(1) space");
                System.out.println("   📝 Insertion Sort - O(n²) time, O(1) space");
                System.out.println("   🔀 Merge Sort - O(n log n) time, O(n) space");
                System.out.println("   ⚡ Quick Sort - O(n log n) average time, O(log n) space");
                System.out.println("   🌲 Heap Sort - O(n log n) time, O(1) space");
                System.out.println("🚀 Application ready!");
                
                try {
                    SortingVisualizerComplete app = new SortingVisualizerComplete();
                    app.setVisible(true);
                    System.out.println("✅ Application window created successfully!");
                } catch (Exception e) {
                    System.err.println("❌ Error starting application: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }
}