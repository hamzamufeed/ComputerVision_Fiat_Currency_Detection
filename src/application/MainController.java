package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MainController {

	@FXML
	private AnchorPane anchorPane;

	@FXML
	private Button browseWeight;

	@FXML
	private Button runDetection;

	@FXML
	private Button viewInput;

	@FXML
	private Button viewOutput;

	@FXML
	private TextField textWeight;

	@FXML
	private Button browseImage;

	@FXML
	private TextField textImage;

	@FXML
	private TextField total;

	@FXML
	private TextField size;

	@FXML
	private TextArea output;

	@FXML
	private ImageView imageView = new ImageView();;

	@FXML
	private Button restart;

	@FXML
	private Button exit;

	static String weightPath = "";
	static String imagePath = "";
	static String outputPath = "";

	@FXML
	void browseWeightFile(ActionEvent event) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select File");
		//File defaultDirectory = new File(System.getProperty("user.dir"));
		File defaultDirectory = new File("C:\\Users\\user\\Desktop\\Java\\YoloV5\\weights");
		fileChooser.setInitialDirectory(defaultDirectory);
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Text Files (*.pt)", "*.pt");
		fileChooser.getExtensionFilters().add(extFilter);
		Stage stage = (Stage)anchorPane.getScene().getWindow();
		stage.getIcons().add(new Image("file:browse.png"));
		File file = fileChooser.showOpenDialog(stage);
		if(file != null && file.isFile()) {
			weightPath = file.getAbsoluteFile().toString();
			textWeight.setText(file.getName());
			output.clear();
			total.clear();
			size.clear();
		}
		else {
			Alert("Please Choose a Weight File");
			return;
		}
	}

	@FXML
	void browseImageFile(ActionEvent event) throws FileNotFoundException {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select File");
		File defaultDirectory = new File("C:\\Users\\user\\Desktop\\Java\\YoloV5\\test2017");
		fileChooser.setInitialDirectory(defaultDirectory);
		Stage stage = (Stage)anchorPane.getScene().getWindow();
		stage.getIcons().add(new Image("file:browse.png"));
		File file = fileChooser.showOpenDialog(stage);
		if(file != null && file.isFile()) {
			imagePath = file.getAbsoluteFile().toString();
			textImage.setText(file.getName());
			InputStream stream = new FileInputStream(imagePath);
			Image image = new Image(stream);
			imageView.setImage(image);
			output.clear();
			total.clear();
			size.clear();
		}
		else {
			Alert("Please Choose an Image");
			return;
		}
	}

	@FXML
	void runDetection(ActionEvent event) throws IOException {
		if(imagePath.equals("") || weightPath.equals(""))
			Alert("Please Choose an Image and a Weight File");
		else {
			//String command = "powershell.exe  your command";
			//Getting the version
			String command = "powershell.exe  python detect1.py --source "+imagePath+" --weights "+weightPath+" --img 416 --save-txt --save-conf";

			// Executing the command
			Process powerShellProcess = Runtime.getRuntime().exec(command);
			// Getting the results
			powerShellProcess.getOutputStream().close();
			String line;
			System.out.println("Standard Output:");
			BufferedReader stdout = new BufferedReader(new InputStreamReader(
					powerShellProcess.getInputStream()));
			int i = 0;
			while ((line = stdout.readLine()) != null) {
				System.out.println(line);
				if(i == 2)
					size.setText(line);
				if(i == 3) {
					String[] res = line.split(", ", 0);
					for(String myStr: res) {
						output.appendText(myStr+"\n");
					}
				}
				if(i == 4)
					total.setText(line);
				if(i == 5) {
					outputPath = line+"\\"+textImage.getText();
					InputStream stream = new FileInputStream(outputPath);
					Image image = new Image(stream);
					imageView.setImage(image);
					output.appendText("\nResults Saved to:\n"+outputPath);
				}
				i++;
			}
			stdout.close();
			System.out.println("Standard Error:");
			BufferedReader stderr = new BufferedReader(new InputStreamReader(
					powerShellProcess.getErrorStream()));
			while ((line = stderr.readLine()) != null) {
				System.out.println(line);
			}
			stderr.close();
			System.out.println("Done");
		}
	}

	@FXML
	void viewInput(ActionEvent event) throws FileNotFoundException {
		if(imagePath.equals(""))
			Alert("Please Choose an Image");
		else {
			InputStream stream = new FileInputStream(imagePath);
			Image image = new Image(stream);
			imageView.setImage(image);
		}
	}

	@FXML
	void viewOutput(ActionEvent event) throws FileNotFoundException {
		if(imagePath.equals("") || weightPath.equals(""))
			Alert("Please Choose an Image and a Weight File");
		else {
			InputStream stream = new FileInputStream(outputPath);
			Image image = new Image(stream);
			imageView.setImage(image);
		}
	}

	public static void Alert(String message) {
		javafx.scene.control.Alert alert = new javafx.scene.control.Alert(AlertType.ERROR);
		alert.setContentText(message);
		alert.setTitle("Error!");
		alert.setHeaderText(null);
		alert.setResizable(false);
		alert.initModality(Modality.APPLICATION_MODAL);
		alert.show();
	}

	@FXML
	void exit(ActionEvent event) {
		System.exit(0);
	}

	@FXML
	void restart(ActionEvent event) throws IOException {
		weightPath = "";
		imagePath = "";
		outputPath = "";
		AnchorPane show = (AnchorPane)FXMLLoader.load(getClass().getResource("MainController.fxml"));
		FadeTransition ft = new FadeTransition(Duration.millis(500), show);
		ft.setFromValue(0.0);
		ft.setToValue(1.0);
		ft.play();
		Scene scene = new Scene(show);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
		window.setScene(scene);
		window.show();
	}

}
