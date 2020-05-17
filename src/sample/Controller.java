package sample;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Controller {

	private static final FileChooser FILE_CHOOSER = new FileChooser();

	@FXML
	private ImageView imageView;

	@FXML
	private Button bntOpen;

	@FXML
	private Label labelFilename;

	@FXML
	private Slider sliderR;

	@FXML
	private Slider sliderG;

	@FXML
	private Slider sliderB;

	@FXML
	private Button btnSave;

	@FXML
	private Button bntRestore;

	private double oldR, oldG, oldB;

	private BufferedImage bufferedImage;

	private int[][] initialR;
	private int[][] initialG;
	private int[][] initialB;

	private String imageExtension;

	@FXML
	public void initialize() {
		FILE_CHOOSER.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image files",
				"*.bmp", "*.BMP","*.png", "*.PNG", "*.jpg", "*.JPG", "*.JPEG"));
		sliderR.valueProperty().addListener((observable, oldValue, newValue) -> changeImageColor((Double) newValue, oldG, oldB));
		sliderG.valueProperty().addListener((observable, oldValue, newValue) -> changeImageColor(oldR, (Double) newValue, oldB));
		sliderB.valueProperty().addListener((observable, oldValue, newValue) -> changeImageColor(oldR, oldG, (Double) newValue));
	}

	private void changeImageColor(double r, double g, double b) {
		if (bufferedImage != null) {
			int width = bufferedImage.getWidth();
			int height = bufferedImage.getHeight();
			for (int i = 0; i < height; i++) {
				for (int j = 0; j < width; j++) {
					int red = r >= 0
							? (int) (initialR[j][i] + ((255 - initialR[j][i]) / 100.0) * r)
							: (int) (initialR[j][i] + (initialR[j][i] / 100.0) * r);
					int green = g >= 0
							? (int) (initialG[j][i] + ((255 - initialG[j][i]) / 100.0) * g)
							: (int) (initialG[j][i] + (initialG[j][i] / 100.0) * g);
					int blue = b >= 0
							? (int) (initialB[j][i] + ((255 - initialB[j][i]) / 100.0) * b)
							: (int) (initialB[j][i] + (initialB[j][i] / 100.0) * b);

					Color newColor = new Color(red, green, blue);
					bufferedImage.setRGB(j, i, newColor.getRGB());
				}
			}
			oldR = r;
			oldG = g;
			oldB = b;
			showImage();
		}
	}

	private void showImage() {
		if (bufferedImage != null) {
			imageView.setImage(null);
			imageView.setImage(SwingFXUtils.toFXImage(bufferedImage, null));
		}
	}

	private void initRGB() {
		if (bufferedImage != null) {
			int width = bufferedImage.getWidth();
			int height = bufferedImage.getHeight();
			initialR = new int[width][height];
			initialG = new int[width][height];
			initialB = new int[width][height];

			for (int i = 0; i < height; i++) {
				for (int j = 0; j < width; j++) {
					Color color = new Color(bufferedImage.getRGB(j, i));
					initialR[j][i] = color.getRed();
					initialG[j][i] = color.getGreen();
					initialB[j][i] = color.getBlue();
				}
			}
		}
	}

	@FXML
	void onClickBtnOpen(ActionEvent event) {
		File file = FILE_CHOOSER.showOpenDialog(bntOpen.getScene().getWindow());
		if (file != null) {
			String filename = file.getName();
			imageExtension = filename.substring(filename.lastIndexOf('.') + 1);
			labelFilename.setText(filename);
			try {
				bufferedImage = ImageIO.read(file);
				initRGB();
				sliderR.setValue(0);
				sliderG.setValue(0);
				sliderB.setValue(0);
				showImage();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@FXML
	void onClickBtnRestore(ActionEvent event) {
		sliderR.setValue(0);
		sliderG.setValue(0);
		sliderB.setValue(0);

	}

	@FXML
	void onClickBtnSave(ActionEvent event) {
		if (bufferedImage != null) {
			FileChooser fileSaver = new FileChooser();
			fileSaver.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image files", "*." + imageExtension));
			fileSaver.setInitialFileName("*." + imageExtension);
			File file = fileSaver.showSaveDialog(btnSave.getScene().getWindow());
			if (file != null) {
				try {
					ImageIO.write(bufferedImage, imageExtension, file);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
