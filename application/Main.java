package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;



import application.utils.FileData;
import application.utils.IpPortPair;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

public class Main extends Application {


	public static final ObservableList<FileData> files = FXCollections.observableArrayList();
	public static final ObservableList<IpPortPair> machines = FXCollections.observableArrayList();
	public static final ObservableList<String> logs = FXCollections.observableArrayList();


	public static Server server = null;


	public static int port;
	public static String DLPATH = "H:\\Projects\\test3\\downloads";
	public static String ULPATH = "H:\\Projects\\test3";

	public static String MACHINES_URL = null;
	
	

	public static void main(String[] args) throws Exception {


		File dl = new File(DLPATH);
		if(!dl.exists() || !dl.isDirectory())
			throw new Exception("DL DIRECTORY IS NOT CORRECT");

		File ul = new File(ULPATH);
		if(!ul.exists() || !ul.isDirectory())
			throw new Exception("UL DIRECTORY IS NOT CORRECT");

		Application.launch(args);

		if(server != null)
			server.stopServer();


	}


	@Override
	public void start(final Stage stage) throws Exception {                
		StackPane root = new StackPane();

		HBox hbox = new HBox(30);       


		VBox vbox = new VBox(10);   


		Region spacer = new Region();
		spacer.setMinHeight(30);
		Region spacer2 = new Region();
		spacer2.setMinHeight(30);

		final Button reqButton = new Button("Search file");
		final TextField reqField = new TextField();


		final String chooseFileText = "Choose Machines JSON file";
		final FileChooser fileCh = new FileChooser();
		final Button fileChButton = new Button(chooseFileText);
		fileChButton.setVisible(false);
		fileChButton.setMaxWidth(Double.MAX_VALUE);
		fileChButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg){
				File file = fileCh.showOpenDialog(stage);
				if(file != null){
					try{
						readInMachines(machines, file);
						fileChButton.setText("Valid file");
						addLog("Machines added from file " + file.getAbsolutePath());
						reqButton.setVisible(true);
						reqField.setVisible(true);


					}catch(Exception e){
						e.printStackTrace();


					}

				}else{
					fileChButton.setText(chooseFileText);
				}
			}
		});
		
		final TextField machinesURLField = new TextField();
		machinesURLField.setVisible(false);
		final Button updMachines = new Button("Update from URL");
		updMachines.setVisible(false);
		updMachines.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg){
				String url = machinesURLField.getText();
				if(url != null){
					try{
						updateMachines(url);
						reqButton.setVisible(true);
						reqField.setVisible(true);

					}catch(Exception e){
						


					}

				}
			}
		});


		final TextField serverPortField = new TextField();
		serverPortField.setMinHeight(20);


		root.getChildren().add(vbox);

		final Button startButton = new Button("Start server");

		startButton.setMaxWidth(Double.MAX_VALUE);


		startButton.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent arg0){

				try{
					port = Integer.parseInt(serverPortField.getText());
					server = new Server(port, ULPATH);
					startButton.setVisible(false);
					serverPortField.setEditable(false);
					fileChButton.setVisible(true);
					updMachines.setVisible(true);
					machinesURLField.setVisible(true);
				}
				catch(Exception e){
					serverPortField.setText("WRONG PORT VALUE");
				}
			}
		});

		vbox.getChildren().add(serverPortField);
		vbox.getChildren().add(startButton);
		vbox.getChildren().add(spacer2);
		vbox.getChildren().add(fileChButton);
		vbox.getChildren().add(machinesURLField);
		vbox.getChildren().add(updMachines);

		vbox.getChildren().add(spacer);


		reqField.setMinHeight(20);

		reqButton.setMaxWidth(Double.MAX_VALUE);

		final String ip = InetAddress.getLocalHost().getHostAddress();
		reqButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg){

				String file = reqField.getText();
				addLog("Searching for file " + file);
				if(file != null){
					clearFileList();
					String id = getNewId();
					for(IpPortPair m : machines){

						new SearchQuery(m.getIp(), Integer.toString(m.getPort()), ip, Integer.toString(port), file, 5 , id, null);

					}
				}

			}
		});

		vbox.getChildren().add(reqButton);
		vbox.getChildren().add(reqField);
		reqButton.setVisible(false);
		reqField.setVisible(false);

		final VBox vbox2 = new VBox(10);  





		final ListView<FileData> filesListView = new ListView<>(files);
		final Button dlButton = new Button("Download");
		dlButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg){

				FileData file = filesListView.getSelectionModel().getSelectedItem();
				if(file != null){

					new GetFileReq(DLPATH, file.ip, Integer.toString(file.port), file.name);

				}

			}
		});



		final ListView<IpPortPair> machinesListView = new ListView<>(machines);
		final ListView<String> logsListView = new ListView<>(logs);

		final ToggleGroup group = new ToggleGroup();

		ToggleButton tb1 = new ToggleButton("Files");
		tb1.setToggleGroup(group);
		tb1.setSelected(true);

		ToggleButton tb2 = new ToggleButton("Machines");
		tb2.setToggleGroup(group);

		ToggleButton tb3 = new ToggleButton("Logs");
		tb3.setToggleGroup(group);


		HBox toggleBox = new HBox();
		toggleBox.getChildren().add(tb1);
		toggleBox.getChildren().add(tb2);
		toggleBox.getChildren().add(tb3);

		vbox2.getChildren().add(toggleBox);

		vbox2.getChildren().add(filesListView);
		vbox2.getChildren().add(dlButton);


		tb1.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent e) {

				if(!vbox2.getChildren().contains(filesListView)){

					if(vbox2.getChildren().contains(machinesListView))
						vbox2.getChildren().remove(machinesListView);
					else
						vbox2.getChildren().remove(logsListView);

					vbox2.getChildren().add(filesListView);
					vbox2.getChildren().add(dlButton);
				}




			}
		});
		tb2.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent e) {
				if(!vbox2.getChildren().contains(machinesListView)){

					if(vbox2.getChildren().contains(filesListView)){
						vbox2.getChildren().remove(filesListView);
						vbox2.getChildren().remove(dlButton);
					}else
						vbox2.getChildren().remove(logsListView);

					vbox2.getChildren().add(machinesListView);

				}

			}
		});

		tb3.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent e) {
				if(!vbox2.getChildren().contains(logsListView)){

					if(vbox2.getChildren().contains(filesListView)){
						vbox2.getChildren().remove(filesListView);
						vbox2.getChildren().remove(dlButton);
					}else
						vbox2.getChildren().remove(machinesListView);

					vbox2.getChildren().add(logsListView);

				}

			}
		});

		HBox.setHgrow(vbox2, Priority.ALWAYS);
		hbox.setPadding(new Insets(20));

		hbox.getChildren().addAll(vbox, vbox2);
		root.getChildren().add(hbox);
		Scene scene = new Scene(root, 700, 400); 

		stage.setTitle("Wazaa");
		stage.setScene(scene);
		stage.show();    
		addLog("Wazaa started");
	}




	public static void clearFileList(){

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				files.clear();
			}
		});
	}

	public synchronized static void addToFilesList(final FileData file){

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				
				IpPortPair p = new IpPortPair();
				p.add(file.ip);
				p.add(Integer.toString(file.port));
				addMachine(p);
				
				
				for(FileData fd : files)
					if(file.equals(fd))
						return;
				
				if(file.id != null && file.id.equals(getId())) 
					files.add(0, file);
				
				
				
			}
		});


	}

	public static synchronized void addLog(final String log){

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				Calendar cal = Calendar.getInstance();
				cal.getTime();
				SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

				logs.add(0, "["+sdf.format(cal.getTime())+"]  " + log);
			}
		});


	}



	private void readInMachines(ObservableList<IpPortPair> machines, File file) throws JsonIOException, JsonSyntaxException, FileNotFoundException{
		Type collectionType = new TypeToken<List<IpPortPair>>(){}.getType();

		List<IpPortPair> data = new Gson().fromJson(new FileReader(file), collectionType);

		for(IpPortPair ipPortPair : data){
			addMachine(ipPortPair);

		}

	}
	
	private synchronized static void addMachine(final IpPortPair ipPortPair){
		Platform.runLater(new Runnable() {
			@Override
			public void run() {

				for(IpPortPair ipPortPair2 : machines)
					if(ipPortPair.getIp().equals(ipPortPair2.getIp()) && (ipPortPair.getPort() == ipPortPair2.getPort()) )
						return;
				machines.add(ipPortPair);
			}
		});
	}

	
	
	private void updateMachines(String url) throws Exception{
		
		URL website = new URL(url);
		String tempname = "temp_machines.txt";
		ReadableByteChannel rbc = Channels.newChannel(website.openStream());
		@SuppressWarnings("resource")
		FileOutputStream fos = new FileOutputStream(tempname);
		fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
		
		
		readInMachines(machines, new File(tempname));
		

	}
	
	
	private static int id = 0;
	private String getNewId(){
		return Integer.toString(++id);
	}
	private static String getId(){
		return Integer.toString(id);
	}



}
