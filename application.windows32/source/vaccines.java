import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class vaccines extends PApplet {

//declaring all variables needed

FloatTable data;
float minDataValue, maxDataValue;
float x1, y1, x2, y2;
float labelX, labelY;
int showLine = 1;
int numRow, numCol;
int currentColumn = 0;
int startYear, endYear;
int[] yearArray;
int percentGap = 20;
PFont fontUsed; 
float[] leftTabValue, rightTabValue; 
float topTabValue, bottomTabValue;
float paddingTab = 10;
int percentGapMinor = 5; 

//setup function
public void setup() {
  

  data = new FloatTable("clean_data_tsv.tsv");
  numRow = data.getRowCount();
  numCol = data.getColumnCount();

  yearArray = PApplet.parseInt(data.getRowNames());
  startYear = yearArray[0];
  endYear = yearArray[yearArray.length - 1];
  minDataValue = 0; 
  maxDataValue = 100;
  // determining boundaries for plot display
  x1 = 150; 
  x2 = width - 80;
  labelX = 50;
  y1 = 100;
  y2 = height - 300;
  labelY = height-250;
  fontUsed = createFont("SansSerif", 20);
  textFont(fontUsed);
  
}

//draw function
public void draw() {
  //setting background to dark color and plot display to white
  background(200);
  fill(255);
  rectMode(CORNERS);
  noStroke();
  rect(x1, y1, x2, y2);
  //calling dfunctions to show the tabs, axis, axis labels
  drawTabs();
  drawYAxisLabels();
  drawXAxisLabels();
  drawPercentLabels();
  //drawing the title, data, and calling function to roll over and show data
  noStroke();
  fill(0xffc16956);
  drawDataTitle();
  drawDisplayKey();
  drawData(currentColumn);
  drawDataPoints(currentColumn);
  drawShowDetails(currentColumn);
}



//Resources used: https://www.cdc.gov/vaccines/pubs/pinkbook/index.html 
//which is Epidemiology and Prevention of Vaccine-Preventable Diseases, a book that the CDC has published.
//- data: https://www.cdc.gov/vaccines/pubs/pinkbook/downloads/appendices/e/coverage-levels.pdf 
//-code: referencing Ben Fry's book and examples, using FloatTable file
//this allows the user to scroll over data points and for that information to be displayed 
public void drawShowDetails(int col) {
  for ( int row = 0; row < numRow; row++ ) {
    if (data.isValid(row, col) ) {
      float value = data.getFloat(row, col);
      float x = map(yearArray[row], startYear, endYear, x1, x2);
      float y = map(value, minDataValue, maxDataValue, y2, y1);
      if ( dist(mouseX, mouseY, x, y) < 6) {
        stroke(0xff3735da);
        strokeWeight(10);
        point(x, y);
        fill(0);
        textSize(20);
        textAlign(CENTER);
        text( nf(value, 0, 2) + "%, " + yearArray[row], x, y -10);
      }
    }
  }
}

//this gets the column names and displays them on the tabs
public void drawTabTitles() {
  fill(0);
  textSize(20);
  textAlign(LEFT);
  String title = data.getColumnName(currentColumn);
  text(title, x1, y1 - 10);
}

//this draws the tabs accoerding to how the columns in the data 
public void drawTabs() {
  rectMode(CORNERS);
  noStroke();
  textSize(20);
  textAlign(LEFT);

  //storing info on tab values
  if (leftTabValue == null) {
    leftTabValue = new float[numCol];
    rightTabValue = new float[numCol];
  }
  float runningX = x1; 
  topTabValue = y1 - textAscent() - 15;
  bottomTabValue = y1;

  for (int col = 0; col < numCol; col++) {
    String title = data.getColumnName(col);
    leftTabValue[col] = runningX; 
    float titleWidth = textWidth(title);
    rightTabValue[col] = leftTabValue[col] + paddingTab + titleWidth + paddingTab;
    //changes tab colors based on which one is selected
    fill(col == currentColumn ? 255 : 224);
    rect(leftTabValue[col], topTabValue, rightTabValue[col], bottomTabValue);
    fill(col == currentColumn ? 0 : 64);
    text(title, runningX + paddingTab, y1 - 10);

    runningX = rightTabValue[col];
  }
}

//mouse functionality: swtiching between different tabs
public void mousePressed() {
  if (mouseY > topTabValue && mouseY < bottomTabValue) {
    for (int col = 0; col < numCol; col++) {
      if (mouseX > leftTabValue[col] && mouseX < rightTabValue[col]) {
        setCurrent(col);
      }
    }
  }
}

//toggling lines on and off
public void keyPressed() {
  if (key == ' ') {
    if (showLine == 0) {
      showLine = 1;
    } else {
      showLine = 0;
    }
  }
}

//determining current column
public void setCurrent(int col) {
  currentColumn = col;
}

public void drawYAxisLabels() {
  fill(0);
  textSize(20);
  textLeading(20);

  textAlign(CENTER, CENTER);
  text("Percent\nVaccinated", labelX, (y1+y2)/2);
  textAlign(CENTER);
  text("Year", (x1+x2)/2, labelY);
}

//drawing x-axis and its labels
public void drawXAxisLabels() {
  fill(0);
  textSize(20);
  textAlign(CENTER);
  stroke(224);
  strokeWeight(1);

  for (int row = 0; row < numRow; row++) {
    float x = map(yearArray[row], startYear, endYear, x1, x2);
    text(yearArray[row], x, y2 + textAscent() + 1);
    if (showLine == 1) {
      line(x, y1, x, y2);
    }
  }
}


public void drawPercentLabels() {
  fill(0);
  textSize(20);
  textAlign(RIGHT);

  stroke(128);
  strokeWeight(1);

  for (float v = minDataValue; v <= maxDataValue; v += percentGapMinor) {
    if (v % percentGapMinor == 0) {     
      float y = map(v, minDataValue, maxDataValue, y2, y1);  
      if (v % percentGap == 0) {        
        float textOffset = textAscent()/2;  
        if (v == minDataValue) {
          textOffset = 0;
        } else if (v == maxDataValue) {
          textOffset = textAscent();
        }
        text(floor(v), x1 - 10, y + textOffset);
        line(x1 - 4, y, x1, y);
      }
    }
  }
}


//drawing the data points and setting conditions to color code 
public void drawDataPoints(int col) { 
  for (int row = 0; row < numRow; row++) { 
    strokeWeight(15);

    if (data.isValid(row, col) ) {
      float value = data.getFloat(row, col);  

      if (value >= 90) {
        stroke(0xff35da37);
        float x = map(yearArray[row], startYear, endYear, x1, x2);
        float y = map(value, minDataValue, maxDataValue, y2, y1);
        point(x, y);
      } else {
        stroke(0xffEC5166);
        float x = map(yearArray[row], startYear, endYear, x1, x2);
        float y = map(value, minDataValue, maxDataValue, y2, y1);
        point(x, y);
      }
    }
  }
}


//draw the data line
public void drawData(int col) {
  beginShape();
  stroke(0xff020e03);
  strokeWeight(2);
  noFill();
  for ( int row = 0; row < numRow; row++ ) {
    if (data.isValid(row, col) ) {
      float value = data.getFloat(row, col);                 
      float x = map(yearArray[row], startYear, endYear, x1, x2);
      float y = map(value, minDataValue, maxDataValue, y2, y1);
      vertex(x, y);
    }
  }
  endShape();
}
//drawing the main title
public void drawDataTitle() {
  textSize(25);
  text("Immunization Rates in United States", 550, y1-50);
}
//drawing the key and information
public void drawDisplayKey() {
  textSize(20);
  stroke(0xff03020e);
  String keyTitle = "Key \n";
  String dtp = "DTP 3+ = 3 or more doses of vaccine against diphtheria, tetanus, and pertussis \n";
  String polio = "Pollio 3+ = 3 or more doses of vaccine against polio \n";
  String mmr = "MMR = at least one dose of vaccine against measles, mumps, and rubella \n";
  String hib = "Hib 3+ = 3 or more doses of vaccine against Haemophilus influenzae type B \n";
  String colorCode = "Red data points represent percent values that fall below the average herd immunity baseline, green data points represent values above it";
  String key= keyTitle+dtp+polio+mmr+hib+colorCode;
  textAlign(LEFT);
  text(key, x1, y2+75);
}
// first line of the file should be the column headers
// first column should be the row titles
// all other values are expected to be floats
// getFloat(0, 0) returns the first data value in the upper lefthand corner
// files should be saved as "text, tab-delimited"
// empty rows are ignored
// extra whitespace is ignored


class FloatTable {
  int rowCount;
  int columnCount;
  float[][] data;
  String[] rowNames;
  String[] columnNames;
  
  
  FloatTable(String filename) {
    String[] rows = loadStrings(filename);
    
    String[] columns = split(rows[0], TAB);
    columnNames = subset(columns, 1); // upper-left corner ignored
    scrubQuotes(columnNames);
    columnCount = columnNames.length;

    rowNames = new String[rows.length-1];
    data = new float[rows.length-1][];

    // start reading at row 1, because the first row was only the column headers
    for (int i = 1; i < rows.length; i++) {
      if (trim(rows[i]).length() == 0) {
        continue; // skip empty rows
      }
      if (rows[i].startsWith("#")) {
        continue;  // skip comment lines
      }

      // split the row on the tabs
      String[] pieces = split(rows[i], TAB);
      scrubQuotes(pieces);
      
      // copy row title
      rowNames[rowCount] = pieces[0];
      // copy data into the table starting at pieces[1]
      data[rowCount] = parseFloat(subset(pieces, 1));

      // increment the number of valid rows found so far
      rowCount++;      
    }
    // resize the 'data' array as necessary
    data = (float[][]) subset(data, 0, rowCount);
  }
  
  
  public void scrubQuotes(String[] array) {
    for (int i = 0; i < array.length; i++) {
      if (array[i].length() > 2) {
        // remove quotes at start and end, if present
        if (array[i].startsWith("\"") && array[i].endsWith("\"")) {
          array[i] = array[i].substring(1, array[i].length() - 1);
        }
      }
      // make double quotes into single quotes
      array[i] = array[i].replaceAll("\"\"", "\"");
    }
  }
  
  
  public int getRowCount() {
    return rowCount;
  }
  
  
  public String getRowName(int rowIndex) {
    return rowNames[rowIndex];
  }
  
  
  public String[] getRowNames() {
    return rowNames;
  }

  
  // Find a row by its name, returns -1 if no row found. 
  // This will return the index of the first row with this name.
  // A more efficient version of this function would put row names
  // into a Hashtable (or HashMap) that would map to an integer for the row.
  public int getRowIndex(String name) {
    for (int i = 0; i < rowCount; i++) {
      if (rowNames[i].equals(name)) {
        return i;
      }
    }
    //println("No row named '" + name + "' was found");
    return -1;
  }
  
  
  // technically, this only returns the number of columns 
  // in the very first row (which will be most accurate)
  public int getColumnCount() {
    return columnCount;
  }
  
  
  public String getColumnName(int colIndex) {
    return columnNames[colIndex];
  }
  
  
  public String[] getColumnNames() {
    return columnNames;
  }


  public float getFloat(int rowIndex, int col) {
    // Remove the 'training wheels' section for greater efficiency
    // It's included here to provide more useful error messages
    
    // begin training wheels
    if ((rowIndex < 0) || (rowIndex >= data.length)) {
      throw new RuntimeException("There is no row " + rowIndex);
    }
    if ((col < 0) || (col >= data[rowIndex].length)) {
      throw new RuntimeException("Row " + rowIndex + " does not have a column " + col);
    }
    // end training wheels
    
    return data[rowIndex][col];
  }
  
  
  public boolean isValid(int row, int col) {
    if (row < 0) return false;
    if (row >= rowCount) return false;
    //if (col >= columnCount) return false;
    if (col >= data[row].length) return false;
    if (col < 0) return false;
    return !Float.isNaN(data[row][col]);
  }


  public float getColumnMin(int col) {
    float m = Float.MAX_VALUE;
    for (int row = 0; row < rowCount; row++) {
      if (isValid(row, col)) {
        if (data[row][col] < m) {
          m = data[row][col];
        }
      }
    }
    return m;
  }


  public float getColumnMax(int col) {
    float m = -Float.MAX_VALUE;
    for (int row = 0; row < rowCount; row++) {
      if (isValid(row, col)) {
        if (data[row][col] > m) {
          m = data[row][col];
        }
      }
    }
    return m;
  }

  
  public float getRowMin(int row) {
    float m = Float.MAX_VALUE;
    for (int col = 0; col < columnCount; col++) {
      if (isValid(row, col)) {
        if (data[row][col] < m) {
          m = data[row][col];
        }
      }
    }
    return m;
  } 


  public float getRowMax(int row) {
    float m = -Float.MAX_VALUE;
    for (int col = 0; col < columnCount; col++) {
      if (isValid(row, col)) {
        if (data[row][col] > m) {
          m = data[row][col];
        }
      }
    }
    return m;
  }


  public float getTableMin() {
    float m = Float.MAX_VALUE;
    for (int row = 0; row < rowCount; row++) {
      for (int col = 0; col < columnCount; col++) {
        if (isValid(row, col)) {
          if (data[row][col] < m) {
            m = data[row][col];
          }
        }
      }
    }
    return m;
  }


  public float getTableMax() {
    float m = -Float.MAX_VALUE;
    for (int row = 0; row < rowCount; row++) {
      for (int col = 0; col < columnCount; col++) {
        if (isValid(row, col)) {
          if (data[row][col] > m) {
            m = data[row][col];
          }
        }
      }
    }
    return m;
  }
}
  public void settings() {  size(1500, 1000);  smooth(); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "vaccines" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
