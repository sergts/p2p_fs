package app;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Main extends Application {

	public static void main(String[] args) {
		Application.launch(args);

	}
	
	public static Server serv;
	public static Client cli;
	public static final ObservableList<String> files = FXCollections.observableArrayList();
	public Main thisApp = this;

	@Override
	public void start(final Stage stage) throws Exception {
		StackPane root = new StackPane();
		Scene scene = new Scene(root, 300, 700);
		
		scene.getStylesheets().add("app/main.css");
		
		VBox vBox = new VBox();
		vBox.getStyleClass().add("mainPanel");
		
		
		
		final ObservableList<String> machines = FXCollections.observableArrayList();
		
		final String _CHOOSE_FILE_TEXT = "Choose Mahines JSON file";
		final FileChooser fileCh = new FileChooser();
		final Button fileChButton = new Button(_CHOOSE_FILE_TEXT);
		fileChButton.setMaxWidth(Double.MAX_VALUE);
		fileChButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg){
				File file = fileCh.showOpenDialog(stage);
				if(file != null){
					try{
						readInMachines(machines, file);
						fileChButton.setText("Valid file");
						
					}catch(Exception e){
						e.printStackTrace();
						

					}
					
				}else{
					fileChButton.setText(_CHOOSE_FILE_TEXT);
				}
			}
		});
		vBox.getChildren().add(fileChButton);
		
		final ListView<String> machinesListView = new ListView<>(machines);
		vBox.getChildren().add(machinesListView);
		
		
		
		
		//final ObservableList<String> files = FXCollections.observableArrayList();
		final ListView<String> filesListView = new ListView<>(files);
		
		final String REQ = "Send request";
		final Button reqButton = new Button("Send request");
		final TextField reqField = new TextField();
		reqField.setMinHeight(20);
		
		reqButton.setMaxWidth(Double.MAX_VALUE);
		
		reqButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg){
				
				String machine = machinesListView.getSelectionModel().getSelectedItem();
				
				if(machine == null){
					
				}
				else{
					
					
					
					cli = new Client(thisApp, machine.split(" ")[1], Integer.parseInt(machine.split(" ")[3])  , reqField.getText());
					
					
					
				}
				
				
				
				
				
				
				
				
				
			}
		});
		
		vBox.getChildren().add(reqButton);
		vBox.getChildren().add(reqField);
		
		vBox.getChildren().add(filesListView);
		
		
		
		
		final TextField serverPortField = new TextField();
		serverPortField.setMinHeight(20);
		
		
		root.getChildren().add(vBox);
		
		Button startButton = new Button("Start server");
		
		startButton.setMaxWidth(Double.MAX_VALUE);
		
		
		startButton.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent arg0){
				
				//here start server
				
				
				
				try{
					int port = Integer.parseInt(serverPortField.getText());
					serv  = new Server(port);
				}
				catch(Exception e){
					serverPortField.setText("WRONG PORT VALUE");
				}
				
				
				
				
				
				
			}
		});
		
		vBox.getChildren().add(startButton);
		vBox.getChildren().add(serverPortField);
		
		
		stage.setTitle("Wazaa");
		stage.setScene(scene);
		stage.show();
		
	}
	
	
	
	
	public void clearFileList(){
		
		Platform.runLater(new Runnable() {
	        @Override
	        public void run() {
	          //javaFX operations should go here
	        	files.clear();
	        }
	   });
	}
	
	public void addToFilesList(final String file){
		
		Platform.runLater(new Runnable() {
	        @Override
	        public void run() {
	          //javaFX operations should go here
	        	files.add(file);
	        }
	   });
		
		
	}
	
	private void readInMachines(ObservableList<String> machines, File file) throws JsonIOException, JsonSyntaxException, FileNotFoundException{
		Type collectionType = new TypeToken<List<IpPortPair>>(){}.getType();
		
		List<IpPortPair> data = new Gson().fromJson(new FileReader(file), collectionType);
		
		machines.clear();
		
		for(IpPortPair ipPortPair : data){
			
			machines.add("ip= " + ipPortPair.getIp() +
					" port= "+ ipPortPair.getPort());
			
		}
		
		/*
		for(List<String> ipPortPair : data){
			System.out.println("ip " + ipPortPair.get(0) +
					", port "+ ipPortPair.get(1));
			
			
		}*/
			
		
		
	}
	
	private static class IpPortPair extends ArrayList<String>{
		public String getIp(){
			return get(0);
		}
		
		public int getPort(){
			return Integer.parseInt(get(1));
		}
	}

}
