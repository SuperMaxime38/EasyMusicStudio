module EZMusicStudio {
	requires javafx.controls;
	
	opens application to javafx.graphics, javafx.fxml;
	requires javafx.base;
    requires javafx.media;
    requires javafx.web;
	requires java.desktop;
}
