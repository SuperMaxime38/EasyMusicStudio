package application;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Main extends Application {
    
    private GridPane timeline = new GridPane(); // GridPane pour la timeline
    private ScrollPane timelineScrollPane = new ScrollPane(); // ScrollPane pour encapsuler la timeline
    private int numRows = 16; // Nombre de lignes, correspondant aux instruments
    private int numColumns = 16; // Nombre de colonnes initial, correspond au nombre de temps ou mesures

    
    //Creation fenetre
    BorderPane root = new BorderPane();
    
    // Création de la scène
    Scene scene = new Scene(root, 900, 850);
    
	// Fichier chargé
	Label file_title = new Label("untitled.txt");
    String filename = "untitled.txt";
    File f;
    

    String[] instruments = {"Piano", "Bass Guitar", "Guitar", "Flute", "Bell", "Chime", "Xylophone", "Bit", 
            "Drum", "Iron Xylophone", "Cow Bell", "Didgeridoo", "Banjo", "Pling", "Sticks", "Snare Drum"};
    
    
    //PLUGIN THING
    String bass = "B";
	String snare = "S";
	String hat = "H";
	String drum = "D";
	String bell = "Bl";
	String flute = "F";
	String chime = "C";
	String guitar = "G";
	String xylophone = "X";
	String iron_xylophone = "IX";
	String cow_bell = "CBl";
	String didgeridoo = "Dg";
	String bit = "BIT";
	String banjo = "Bj";
	String pling = "P";
	String harp = "Hr";
    
	//Note value
    TextField noteValueField = new TextField("0");
    
    //Changment valeur simple
    private Button lastModifiedCell; // Variable pour stocker la dernière cellule modifiée
    
    
    //SOUNDS
    private Map<Instrument, AudioClip> soundMap = new HashMap<>();
	
    @Override
    public void start(Stage primaryStage) {
    	
    	//Load sounds
    	loadSounds();
    	
    	//File Label
    	HBox title = new HBox(file_title);
    	title.setAlignment(Pos.CENTER);
    	
        // Panneau d'instruments
        VBox instrumentPanel = new VBox();
        
        for (String instrument : instruments) {
            Button instrumentButton = new Button(instrument);
            instrumentButton.setStyle(getClickedCellInstrumentColor(instrument) + "-fx-text-fill: white;-fx-font-weight: bold;");
            instrumentButton.setPrefWidth(120);
            instrumentButton.setOnAction(event -> {
                System.out.println("Instrument sélectionné : " + instrument);
            });
            instrumentPanel.getChildren().add(instrumentButton);
        }

        // Champ de saisie pour la durée de la musique
        Label durationLabel = new Label("Durée de la musique (en tick) :");
        TextField durationField = new TextField(String.valueOf(numColumns));
        Button setDurationButton = new Button("Mettre à jour la durée");

        // Action du bouton de mise à jour de la durée
        setDurationButton.setOnAction(event -> {
            String durationText = durationField.getText();
            int duration = Integer.parseInt(durationText);
            updateTimeline(duration);
        });

        // Panneau de contrôle
        HBox controlPanel = new HBox();
        Button playButton = new Button("Play");
        Button stopButton = new Button("Stop");
        Button saveButton = new Button("Save");
        Button loadButton = new Button("Load");
        
        playButton.setOnAction(event -> {
            System.out.println("Lecture de la musique");
        });
        
        stopButton.setOnAction(event -> {
            System.out.println("Arrêt de la musique");
        });
        
        // **Action du bouton Load**
        loadButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Ouvrir un fichier EZMusic");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers EZMusic", "*.txt"));
            f = fileChooser.showOpenDialog(primaryStage);

            if (f != null) {
                System.out.println("Fichier sélectionné : " + f.getAbsolutePath());
                filename = f.getName();
                file_title.setText(filename);
                List<Pair> music = new ArrayList<>();
                Scanner myReader;
                initializeTimeline();
				try {
					myReader = new Scanner(f);
					int columns = 0;
					while (myReader.hasNextLine()) {
	    		        String line = myReader.nextLine();
	    		        if(line.equals("")) {
	    		        	music.add(null);
	    		        } else {
	    		        	String[] notes = line.split(",");
	    			        
	    			        for(String note : notes) {
	    			        	if(getInstrument(note.split(":")[0]) == null) {
	    			        		music.add(null);
	    			        	} else {
	    			        		Instrument instr = getInstrument(note.split(":")[0]);
	    				        	int value = Integer.valueOf(note.split(":")[1]);
	    				        	music.add(new Pair(instr, value, columns));
	    			        	}
	    			        }
	    		        }

						columns++;
	    		        
	    		    }
	    		    myReader.close();
	    		    
	    		    //Render
	    		    updateTimeline(columns);
	    		    durationField.setText(String.valueOf(columns));
	    		    for(Pair p : music) {
	    		    	if(p != null) {
	    		    		Instrument instr = (Instrument) p.key;
		    		    	int note = (int) p.value;
		    		    	
		    		    	int row = getinstrumentRow(instr);
		    		    	setTimelineButtonState(row, p.column, true);
		    		    	setCellValue(row, p.column, note);
	    		    	}
	    		    	
	    		    }
	    		    
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        });
        saveButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Music File");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));

            // Affiche la boîte de dialogue pour choisir où enregistrer le fichier
            File file = fileChooser.showSaveDialog(primaryStage);
            if (file != null) {
                saveTimelineToFile(timeline, file);
            }
        });
        
        // Ajout du champ de texte pour la valeur de la note
        Label noteValueLabel = new Label("Valeur de la note (0-24) :");
        noteValueField.setPrefWidth(10);

        // Ajout de ce champ au panneau à gauche du control panel
        VBox noteValuePanel = new VBox(noteValueLabel, noteValueField);
        noteValuePanel.setSpacing(10);
        noteValuePanel.setPadding(new Insets(10));
        
        controlPanel.getChildren().addAll(playButton, stopButton, saveButton, loadButton);
        controlPanel.setSpacing(20);
        controlPanel.setPadding(new Insets(10));
        controlPanel.setAlignment(Pos.CENTER);
        
        instrumentPanel.setSpacing(21);
        instrumentPanel.setPadding(new Insets(10));

        // Panneau d'entrée de la durée
        VBox durationPanel = new VBox(durationLabel, durationField, setDurationButton);
        durationPanel.setSpacing(10);
        durationPanel.setPadding(new Insets(10));
        durationPanel.setAlignment(Pos.CENTER);
        
        HBox navbar = new HBox(noteValuePanel, controlPanel, durationPanel);
        navbar.setAlignment(Pos.BOTTOM_CENTER);
        navbar.setSpacing(160);
        

        // Timeline
        timelineScrollPane.setContent(timeline);
        timelineScrollPane.setFitToHeight(true); // Ajuste la hauteur du ScrollPane à celle de la fenêtre
        timelineScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS); // Toujours afficher la scrollbar horizontale

        // Timeline initiale
        initializeTimeline();

        // Configuration de la fenêtre principale
        root.setTop(title);
        root.setCenter(timelineScrollPane);
        root.setLeft(instrumentPanel);
        //root.setRight(durationPanel);
        root.setBottom(navbar);

        // Configuration et affichage de la fenêtre principale
        primaryStage.setTitle("EZMusicStudio | Note Block Composer");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    private void saveTimelineToFile(GridPane timeline, File file) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (int col = 0; col < timeline.getColumnCount(); col++) {
                for (int row = 0; row < timeline.getRowCount(); row++) {
                    Button cell = (Button) getNodeFromGridPane(timeline, col, row);
                    if(cell != null) {
                    	if(!cell.getText().isEmpty()) {
                            String note = cell.getText();
                            String instrument = getInstrumentAsString(getRowByInstrument(row));
                            writer.write(instrument+":"+note + ",");
                    	}
                    }
                }
                writer.newLine();
            }
            System.out.println("Fichier enregistré : " + file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Initialise la timeline avec les paramètres par défaut
    private void initializeTimeline() {
        timeline.setPadding(new Insets(10));
        timeline.setHgap(5);
        timeline.setVgap(5);
        timeline.setStyle("-fx-text-fill: white;-fx-font-weight: bold;");
        updateTimeline(numColumns); // Mise à jour initiale avec le nombre de colonnes par défaut
    }

    // Structure pour sauvegarder les valeurs des cellules
    private Map<String, String> timelineState = new HashMap<>();
    
    // Met à jour la timeline en fonction de la durée
    private void updateTimeline(int duration) {
    	// Sauvegarder l'état actuel de la timeline
        saveTimelineState();

        // Suppression des anciennes colonnes
        timeline.getChildren().clear();

        // Calcul du nombre de colonnes en fonction de la durée
        numColumns = duration;

        // Remplissage de la timeline avec des boutons
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numColumns; col++) {
                int roww = row;
                Button cell = new Button();
                cell.setPrefSize(40, 40); // Taille de chaque cellule
                String color = getInstrumentColor(instruments[roww]);
                cell.setStyle(color);

                // Récupérer la valeur précédemment sauvegardée, le cas échéant
                String cellKey = row + "-" + col;
                if (timelineState.containsKey(cellKey)) {
                    cell.setText(timelineState.get(cellKey));
                    cell.setStyle(getClickedCellInstrumentColor(instruments[roww]));
                }

                cell.setOnAction(event -> {
                    String noteValueText = noteValueField.getText();
                    try {
                        int noteValue = Integer.parseInt(noteValueText);
                        if (noteValue >= 0 && noteValue <= 24) {
                            if (cell.getText().isEmpty()) {
                                cell.setText(String.valueOf(noteValue));  // Affiche la valeur de la note sur le bouton
                                cell.setStyle(getClickedCellInstrumentColor(instruments[roww]));
                            } else {
                                cell.setText(""); // Enlève la valeur de la note
                                cell.setStyle(color); // Remet le style par défaut
                            }
                            lastModifiedCell = cell; // Mémorise la dernière cellule modifiée
                        } else {
                            System.out.println("Valeur de note invalide. Entrez une valeur entre 0 et 24.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Veuillez entrer un nombre valide.");
                    }
                    // Redonne le focus à la scène pour capturer les événements de clavier
                    cell.getScene().getRoot().requestFocus();
                });

                timeline.add(cell, col, row);
            }
        }

        // Gestion des touches "flèche du haut" et "flèche du bas"
        scene.setOnKeyPressed(event -> {
            if (lastModifiedCell != null) {
                String currentText = lastModifiedCell.getText();
                if (!currentText.isEmpty()) {
                    int currentValue = Integer.parseInt(currentText);
                    switch (event.getCode()) {
                        case UP:
                            if (currentValue < 24) {
                                lastModifiedCell.setText(String.valueOf(currentValue + 1));
                            }
                            break;
                        case DOWN:
                            if (currentValue > 0) {
                                lastModifiedCell.setText(String.valueOf(currentValue - 1));
                            }
                            break;
                        default:
                            break;
                    }
                }
            }
        });
    }
    

 	// Méthode pour sauvegarder l'état actuel de la timeline
 	private void saveTimelineState() {
 		timelineState.clear();
 		for (Node node : timeline.getChildren()) {
 			if (node instanceof Button) {
 				Button cell = (Button) node;
 				int row = GridPane.getRowIndex(cell);
 				int col = GridPane.getColumnIndex(cell);
 				String cellKey = row + "-" + col;
 				if (!cell.getText().isEmpty()) {
 					timelineState.put(cellKey, cell.getText());
 				}
 			}
 		}
 	}
    
    // Méthode pour obtenir la couleur par instrument (avant clic)
    public static String getInstrumentColor(String instr) {
        // Liste des couleurs par instrument
        switch(instr) {
            case "Piano": return "-fx-background-color: #b6ff8c;";
            case "Bass Guitar": return "-fx-background-color: #ffeb91;";
            case "Guitar": return "-fx-background-color: #ff5555;";
            case "Flute": return "-fx-background-color: #c683ff;";
            case "Bell": return "-fx-background-color: #dfc835;";
            case "Chime": return "-fx-background-color: #c0f000;";
            case "Xylophone": return "-fx-background-color: #d68500;";
            case "Bit": return "-fx-background-color: #66ffba;";
            case "Drum": return "-fx-background-color: #7fe2ff;";
            case "Iron Xylophone": return "-fx-background-color: #c5c5c5;";
            case "Cow Bell": return "-fx-background-color: #3ee400;";
            case "Didgeridoo": return "-fx-background-color: #6455c1;";
            case "Banjo": return "-fx-background-color: #756a7d;";
            case "Pling": return "-fx-background-color: #ab5bbf;";
            case "Sticks": return "-fx-background-color: #ff9e00;";
            case "Snare Drum": return "-fx-background-color: #ff588b;";
            default: return "-fx-background-color: gray;";
        }
    }
    
    // Méthode pour obtenir la couleur par instrument (après clic)
    public static String getClickedCellInstrumentColor(String instr) {
        switch(instr) {
            case "Piano": return "-fx-background-color: green;";
            case "Bass Guitar": return "-fx-background-color: #c2aa3d;";
            case "Guitar": return "-fx-background-color: #b90000;";
            case "Flute": return "-fx-background-color: #8214e0;";
            case "Bell": return "-fx-background-color: #958000;";
            case "Chime": return "-fx-background-color: #8aac00;";
            case "Xylophone": return "-fx-background-color: #835200;";
            case "Bit": return "-fx-background-color: #0cd077;";
            case "Drum": return "-fx-background-color: #2c9dbf;";
            case "Iron Xylophone": return "-fx-background-color: #5f5f5f;";
            case "Cow Bell": return "-fx-background-color: #53a036;";
            case "Didgeridoo": return "-fx-background-color: #3419d9;";
            case "Banjo": return "-fx-background-color: #42334d;";
            case "Pling": return "-fx-background-color: #9b12bd;";
            case "Sticks": return "-fx-background-color: #c58000;";
            case "Snare Drum": return "-fx-background-color: #b3073c;";
            default: return "-fx-background-color: gray;";
        }
    }
    private Instrument getInstrument(String instr) { //COPIED FROM PLUGIN
		//CAN'T SWITCH BCS IDK
		Instrument instrument;
		if(instr == bass) {
			instrument = Instrument.BASS_GUITAR;
		} else if(instr.equals(snare)) {
			instrument = Instrument.SNARE_DRUM;
		} else if(instr.equals(hat)) {
			instrument = Instrument.STICKS;
		} else if(instr.equals(drum)) {
			instrument = Instrument.BASS_DRUM;
		} else if(instr.equals(bell)) {
			instrument = Instrument.BELL;
		} else if(instr.equals(flute)) {
			instrument = Instrument.FLUTE;
		} else if(instr.equals(chime)) {
			instrument = Instrument.CHIME;
		} else if(instr.equals(guitar)) {
			instrument = Instrument.GUITAR;
		} else if(instr.equals(xylophone)) {
			instrument = Instrument.XYLOPHONE;
		} else if(instr.equals(iron_xylophone)) {
			instrument = Instrument.IRON_XYLOPHONE;
		} else if(instr.equals(cow_bell)) {
			instrument = Instrument.COW_BELL;
		} else if(instr.equals(didgeridoo)) {
			instrument = Instrument.DIDGERIDOO;
		} else if(instr.equals(bit)) {
			instrument = Instrument.BIT;
		} else if(instr.equals(banjo)) {
			instrument = Instrument.BANJO;
		} else if(instr.equals(pling)) {
			instrument = Instrument.PLING;
		} else if(instr.equals("")) {
			return null;
		} else { //HARP/PIANO DEFAULT SOUND
			instrument = Instrument.PIANO;
		}
		
		return instrument;
	}
    
    private String getInstrumentAsString(Instrument instr) {
    	switch(instr) {
		default:
			return "Hr";
		case BASS_GUITAR:
			return bass;
		case SNARE_DRUM:
			return snare;
		case STICKS:
			return hat;
		case BASS_DRUM:
			return drum;
		case BELL:
			return bell;
		case FLUTE:
			return flute;
		case CHIME:
			return chime;
		case GUITAR:
			return guitar;
		case XYLOPHONE:
			return xylophone;
		case IRON_XYLOPHONE:
			return iron_xylophone;
		case COW_BELL:
			return cow_bell;
		case DIDGERIDOO:
			return didgeridoo;
		case BIT:
			return bit;
		case BANJO:
			return banjo;
		case PLING:
			return pling;
    	
    	}
    }
    
    public int getinstrumentRow(Instrument instr) {
    	switch(instr) {
		default:
			return 0;
		case BASS_GUITAR:
			return 1;
		case GUITAR:
			return 2;
		case FLUTE:
			return 3;
		case BELL:
			return 4;
		case CHIME:
			return 5;
		case XYLOPHONE:
			return 6;
		case BIT:
			return 7;
		case BASS_DRUM:
			return 8;
		case IRON_XYLOPHONE:
			return 9;
		case COW_BELL:
			return 10;
		case DIDGERIDOO:
			return 11;
		case BANJO:
			return 12;
		case PLING:
			return 13;
		case STICKS:
			return 14;
		case SNARE_DRUM:
			return 15;
    	}
    }
    
    public Instrument getRowByInstrument(int row) {
    	switch(row) {
    	default:
    		return Instrument.PIANO;
    	case 1:
    		return Instrument.BASS_GUITAR;
    	case 2:
    		return Instrument.GUITAR;
    	case 3:
    		return Instrument.FLUTE;
    	case 4:
    		return Instrument.BELL;
    	case 5:
    		return Instrument.CHIME;
    	case 6:
    		return Instrument.XYLOPHONE;
    	case 7:
    		return Instrument.BIT;
    	case 8:
    		return Instrument.BASS_DRUM;
    	case 9:
    		return Instrument.IRON_XYLOPHONE;
    	case 10:
    		return Instrument.COW_BELL;
    	case 11:
    		return Instrument.DIDGERIDOO;
    	case 12:
    		return Instrument.BANJO;
    	case 13:
    		return Instrument.PLING;
    	case 14:
    		return Instrument.STICKS;
    	case 15:
    		return Instrument.SNARE_DRUM;
    	}
    }
    
    public void setTimelineButtonState(int row, int col, boolean active) {
        // Récupère le bouton à la position spécifiée
        Button button = (Button) getNodeFromGridPane(timeline, col, row);

        if (button != null) {
            String instrument = instruments[row];
            String activeColor = getClickedCellInstrumentColor(instrument);
            String defaultColor = getInstrumentColor(instrument);

            // Change le style du bouton en fonction de l'état
            if (active) {
                button.setStyle(activeColor);
            } else {
                button.setStyle(defaultColor);
            }
        }
    }
    
    public void setCellValue(int row, int col, int value) {
        Button button = (Button) getNodeFromGridPane(timeline, col, row);
        if(button != null) button.setText(String.valueOf(value));
    }

    // Méthode auxiliaire pour récupérer un noeud spécifique dans une GridPane
    private Node getNodeFromGridPane(GridPane gridPane, int col, int row) {
        for (Node node : gridPane.getChildren()) {
            if (GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row) {
                return node;
            }
        }
        return null;
    }
    
    private void loadSounds() {
    	
        for (String instrument : instruments) {
        	try {
        		instrument = instrument.replaceAll(" ", "_");
            	System.out.println("trying to load " + instrument + ".wav");
            	URL is = Main.class.getResource("/" + instrument + ".wav");
            	File f = new File(is.toURI());
            	
            	//AudioClip audioClip = AudioClipLoader.loadAudioClipFromInputStream(is);
            	//soundMap.put(getInstrument(instrument), audioClip);
            	
        	}catch (URISyntaxException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
