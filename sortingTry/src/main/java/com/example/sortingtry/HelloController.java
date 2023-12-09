package com.example.sortingtry;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;

public class HelloController implements Initializable {
    @FXML
    private Pane pane;
    @FXML
    private AnchorPane rootPane;
    @FXML
    private Button startButton;
    @FXML
    private Button stopButton;
    @FXML
    private Button compareButton;
    @FXML
    private Slider speedSlider;
    @FXML
    private Slider sizeSlider;
    @FXML
    private Text created;
    @FXML
    private Text sorest;
    @FXML
    private Text speedTxt;
    @FXML
    private Text sizeTxt;
    @FXML
    private Text showSpeed;
    @FXML
    private Text showSize;
    @FXML
    private Text showCompares;
    @FXML
    private Text showReplacments;
    @FXML
    private ColorPicker pickColor;
    @FXML
    private ComboBox<String> sortings;
    Rectangle[] recs;
    String[] sortingAlgorithms = {"Selection Sort", "Bubble Sort", "Quick Sort", "Insertion Sort"};
    String sort;
    boolean sorted;
    boolean changeAble;
    int noOfComparisons;
    int noOfReplacments;
    int randomNumber;
    int windowWidth;
    int size, gap, speed;
    volatile boolean play;
    Color barsMainColor;
    Color swapAnmColor;
    Color barsSortedColor;
    Color selectedBarsColor;
    Thread currentThread;
    CountDownLatch latch;

    public void changeToLigthColors() {
        barsMainColor = pickColor.getValue();
        swapAnmColor = Color.rgb(66, 66, 200);
        barsSortedColor = Color.rgb(10, 150, 10);
        sorest.setFill(Color.rgb(255, 255, 255));
        created.setFill(Color.rgb(255, 255, 255));
        selectedBarsColor = Color.rgb(255, 120, 70);
        showSpeed.setFill(Color.rgb(255, 255, 255));
        showSize.setFill(Color.rgb(255, 255, 255));
        showReplacments.setFill(Color.rgb(255, 255, 255));
        showCompares.setFill(Color.rgb(255, 255, 255));
        rootPane.setBackground(new Background(new BackgroundFill(Color.rgb(46, 46, 46), CornerRadii.EMPTY, Insets.EMPTY)));
    }

    public void changeToDarkColors() {
        barsMainColor = pickColor.getValue();
        sorest.setFill(Color.rgb(46, 46, 46));
        created.setFill(Color.rgb(46, 46, 46));
        showSpeed.setFill(Color.rgb(46, 46, 46));
        showSize.setFill(Color.rgb(46, 46, 46));
        showReplacments.setFill(Color.rgb(46, 46, 46));
        showCompares.setFill(Color.rgb(46, 46, 46));
        rootPane.setBackground(new Background(new BackgroundFill(Color.rgb(200, 200, 200), CornerRadii.EMPTY, Insets.EMPTY)));
    }

    public void AssignInitialValues() {
        gap = 2;
        size = 25;
        speed = 100;
        noOfReplacments = 0;
        changeAble = true;
        windowWidth = 1000;
        noOfComparisons = 0;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        AssignInitialValues();
        changeToLigthColors();
        showSpeed.setText("Speed:" + (int) speedSlider.getValue() + "ms");
        showSize.setText("Size:" + (int) sizeSlider.getValue());
        sizeSlider.setOnMouseReleased(event -> {
            int oldSize = size;
            size = (int) sizeSlider.getValue();
            if (oldSize != size) CreatRecs();
            showSize.setText("Size:" + (int) sizeSlider.getValue());
        });
        speedSlider.setOnMouseReleased(event -> {
            speed = (int) speedSlider.getValue();
            showSpeed.setText("Speed:" + (int) speedSlider.getValue() + "ms");
        });
        sortings.getItems().addAll(sortingAlgorithms);
        sortings.setOnAction(event -> sort = sortings.getValue());
        sort = sortings.getItems().get(0);
        pickColor.setOnAction(event -> {
                    barsMainColor = pickColor.getValue();
                    double luminance = 0.299 * barsMainColor.getRed() + 0.587 * barsMainColor.getGreen() + 0.114 * barsMainColor.getBlue();
                    if (luminance < 0.5) changeToDarkColors();
                    else changeToLigthColors();
                    CreatRecs();
                }
        );
        CreatRecs();
    }

    public void start() {
        play = true;
        noOfReplacments = 0;
        noOfComparisons = 0;
        showCompares.setText("Number of Comparisons:" + noOfComparisons);
        showReplacments.setText("Number of Position Changes:" + noOfReplacments);

        switch (sort) {
            case "Insertion Sort":
                insertionSort();
                break;
            case "Quick Sort":
                quickSort();
                break;
            case "Bubble Sort":
                bubbleSort();
                break;
            case "Selection Sort":
                selectionSort();
                break;
        }
    }

    public void stop() {
        play = false;
        currentThread.interrupt();
        disable(false);
    }

    public void delay(int number) {
        try {
            Thread.sleep(number);
        } catch (InterruptedException ignored) {
        }
    }

    public void disable(boolean decision) {
        pickColor.setDisable(decision);
        startButton.setDisable(decision);
        speedSlider.setDisable(decision);
        compareButton.setDisable(decision);
        sizeSlider.setDisable(decision);
        sortings.setDisable(decision);
    }

    public void CreatRecs() {
        if (recs != null) {
            sorted = false;
            rootPane.getChildren().clear();
            rootPane.getChildren().addAll(pane, speedTxt, sorest, created, sizeTxt, startButton, speedSlider, showSpeed,
                    sizeSlider, stopButton, compareButton, sortings, showSize, showReplacments, showCompares);
            recs = null;
            CreatRecs();
        } else {
            recs = new Rectangle[windowWidth / (size + gap)];
            for (int i = 0, j = 1; i < recs.length; i++, j += size + gap) {
                randomNumber = (int) (Math.random() * 500 + 1);
                Rectangle rec = new Rectangle();
                recs[i] = rec;
                rec.setWidth(size);
                rec.setFill(barsMainColor);
                rec.setHeight(randomNumber);
                rec.setY(590 - randomNumber);
                rec.setX(2 + i * (size + gap));
                rootPane.getChildren().add(recs[i]);
            }
        }
    }

    public ParallelTransition swapAnm(Rectangle r1, Rectangle r2) {
        double a1 = r1.getBoundsInParent().getMinX();
        double a2 = r2.getBoundsInParent().getMinX();
        TranslateTransition tr1 = new TranslateTransition(Duration.millis(speed), r1);
        tr1.setByX(a2 - a1);
        TranslateTransition tr2 = new TranslateTransition(Duration.millis(speed), r2);
        tr2.setByX(a1 - a2);
        return new ParallelTransition(tr1, tr2);
    }

    public void selectionSort() {
        play = true;
        disable(true);
        currentThread = new Thread(() -> {
            for (int i = 0; i < recs.length; i++) {
                for (int j = i; j < recs.length; j++) {
                    if (play) {
                        int finalI = i;
                        int finalJ = j;
                        Platform.runLater(() -> {
                            noOfComparisons++;
                            recs[finalI].setFill(selectedBarsColor);
                            recs[finalJ].setFill(selectedBarsColor);
                            showCompares.setText("Number of Comparisons:" + noOfComparisons);
                        });
                        delay(speed);
                        if (recs[i].getHeight() > recs[j].getHeight()) {
                            latch = new CountDownLatch(1);
                            Platform.runLater(() -> {
                                noOfReplacments++;
                                recs[finalI].setFill(swapAnmColor);
                                recs[finalJ].setFill(swapAnmColor);
                                delay(speed);
                                ParallelTransition parel = swapAnm(recs[finalI], recs[finalJ]);
                                Rectangle temp = recs[finalI];
                                recs[finalI] = recs[finalJ];
                                recs[finalJ] = temp;
                                parel.play();
                                parel.setOnFinished(e -> latch.countDown());
                                showReplacments.setText("Number of Position Changes:" + noOfReplacments);
                            });
                            try {
                                latch.await();
                            } catch (Exception ignored) {
                            }
                        }
                        Platform.runLater(() -> {
                            recs[finalI].setFill(barsMainColor);
                            recs[finalJ].setFill(barsMainColor);
                            if (finalJ == recs.length - 1) recs[finalI].setFill(barsSortedColor);
                        });
                    } else {
                        disable(false);
                    }
                }
            }
            disable(false);
        });
        currentThread.start();
    }

    public void bubbleSort() {
        play = true;
        disable(true);
        currentThread = new Thread(() -> {
            for (int i = 0; i < recs.length; i++) {
                for (int j = 0; j < recs.length - i - 1; j++) {
                    if (play) {
                        int finale = i;
                        int finalI = j;
                        int finalJ = j + 1;
                        Platform.runLater(() -> {
                            noOfComparisons++;
                            recs[finalI].setFill(selectedBarsColor);
                            recs[finalJ].setFill(selectedBarsColor);
                            showCompares.setText("Number of Comparisons:" + noOfComparisons);
                        });
                        delay(speed);
                        if (recs[j].getHeight() > recs[j + 1].getHeight()) {
                            latch = new CountDownLatch(1);
                            Platform.runLater(() -> {
                                noOfReplacments++;
                                recs[finalI].setFill(swapAnmColor);
                                recs[finalJ].setFill(swapAnmColor);
                                delay(speed);
                                ParallelTransition parel = swapAnm(recs[finalI], recs[finalJ]);
                                Rectangle temp = recs[finalJ];
                                recs[finalJ] = recs[finalI];
                                recs[finalI] = temp;
                                parel.play();
                                parel.setOnFinished(e -> latch.countDown());
                                showReplacments.setText("Number of Position Changes:" + noOfReplacments);
                            });
                            try {
                                latch.await();
                            } catch (Exception ignored) {
                            }
                        }
                        Platform.runLater(() -> {
                            recs[finalI].setFill(barsMainColor);
                            recs[finalJ].setFill(barsMainColor);
                            if (finalJ == recs.length - finale - 1) recs[finalJ].setFill(barsSortedColor);
                            if (finalI == 0 && finalJ == recs.length - finale - 1)
                                recs[finalJ - 1].setFill(barsSortedColor);
                        });
                    } else {
                        disable(false);
                    }
                }
            }
            disable(false);
        });
        currentThread.start();
    }

    public void quickSort() {
        play = true;
        disable(true);
        latch = new CountDownLatch(1);

        currentThread = new Thread(() -> {
            quickSortHelper(0, recs.length - 1);
            latch.countDown();
            for (int i = 0; i < recs.length; i++) {
                int finalI = i;
                Platform.runLater(() -> {
                    recs[finalI].setFill(barsSortedColor);
                });
                delay(20);
            }
            disable(false);
        });
        currentThread.start();
    }

    private void quickSortHelper(int low, int high) {
        if (low < high) {
            try {
                int pivotIndex = partition(low, high);
                quickSortHelper(low, pivotIndex - 1);
                quickSortHelper(pivotIndex + 1, high);
            }catch (StackOverflowError e){}
        }
    }

    private int partition(int low, int high) {
        int pivot = (int) recs[high].getHeight();
        int i = low - 1;

        for (int j = low; j < high; j++) {
            if (play) {
                int finalJ = j;
                int finalI = i;

                Platform.runLater(() -> {
                    recs[high].setFill(Color.YELLOW);
                    recs[finalJ].setFill(selectedBarsColor);
                    recs[finalI + 1].setFill(selectedBarsColor);
                    noOfComparisons++;
                    showCompares.setText("Number of Comparisons:" + noOfComparisons);
                });
                delay(speed);
                if ((int) recs[j].getHeight() < pivot) {
                    i++;
                    latch = new CountDownLatch(1);
                    int finalI2 = i;
                    int finalJ2 = j;
                    Platform.runLater(() -> {
                        recs[finalI2].setFill(swapAnmColor);
                        recs[finalJ2].setFill(swapAnmColor);
                        delay(speed);
                        ParallelTransition parel = swapAnm(recs[finalI2], recs[finalJ2]);
                        Rectangle temp = recs[finalI2];
                        recs[finalI2] = recs[finalJ2];
                        recs[finalJ2] = temp;
                        parel.play();
                        showReplacments.setText("Number of Position Changes:" + (++noOfReplacments));
                        parel.setOnFinished(e -> {
                            latch.countDown();
                        });

                    });
                    try {
                        latch.await();
                    } catch (InterruptedException ignored) {
                    }
                    delay(speed);
                    recs[finalJ].setFill(barsMainColor);
                    recs[finalI + 1].setFill(barsMainColor);
                }
            } else {
                disable(false);
                return -1;
            }
        }

        int finalI3 = i + 1;
        int finalHigh = high;
        latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            recs[finalI3].setFill(swapAnmColor);
            recs[finalHigh].setFill(swapAnmColor);
            delay(speed);
            ParallelTransition parel = swapAnm(recs[finalI3], recs[finalHigh]);
            Rectangle temp = recs[finalI3];
            recs[finalI3] = recs[finalHigh];
            recs[finalHigh] = temp;
            parel.play();
            showReplacments.setText("Number of Position Changes:" + (++noOfReplacments));
            parel.setOnFinished(e -> {
                latch.countDown();
            });
        });
        try {
            latch.await();
        } catch (InterruptedException ignored) {
        }
        delay(speed);
        Platform.runLater(() -> {
            recs[finalI3].setFill(barsMainColor);
            recs[finalHigh].setFill(barsMainColor);
        });
        return i + 1;
    }

    public void insertionSort() {
        play = true;
        disable(true);
        currentThread = new Thread(() -> {
            for (int i = 1; i < recs.length; i++) {
                if (play) {
                    int key = (int) recs[i].getHeight();
                    int j = i - 1;
                    int finalI = i;
                    Platform.runLater(() -> {
                        recs[finalI - 1].setFill(selectedBarsColor); // Şu anda karşılaştırılan elemanı belirtmek için renk değiştir
                        noOfComparisons++;
                        showCompares.setText("Number of Comparisons:" + noOfComparisons);
                    });
                    delay(speed);
                    while (j >= 0 && (int) recs[j].getHeight() > key) {
                        int finalJ = j;
                        latch = new CountDownLatch(1);
                        Platform.runLater(() -> {
                            recs[finalJ + 1].setFill(selectedBarsColor);
                            recs[finalJ].setFill(selectedBarsColor);
                            delay(speed);
                            ParallelTransition parel = swapAnm(recs[finalJ], recs[finalJ + 1]);
                            Rectangle temp = recs[finalJ];
                            recs[finalJ] = recs[finalJ + 1];
                            recs[finalJ + 1] = temp;
                            parel.play();
                            parel.setOnFinished(e -> {
                                recs[finalJ].setFill(barsMainColor);
                                recs[finalJ + 1].setFill(barsMainColor);
                                latch.countDown();
                            });
                            noOfReplacments++;
                            showReplacments.setText("Number of Position Changes:" + noOfReplacments);
                        });
                        try {
                            latch.await();
                        } catch (InterruptedException ignored) {
                        }

                        j = j - 1;
                    }
                    if (finalI == recs.length - 1) disable(false);
                } else return;
            }
            for (int i = 0; i < recs.length; i++) {
                int finalI = i;
                Platform.runLater(() -> {
                    recs[finalI].setFill(barsSortedColor); // Sıralanan elemanın rengini değiştir
                });
                delay(5);
            }
        });
        currentThread.start();
    }
}
