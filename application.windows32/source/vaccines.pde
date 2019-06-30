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
void setup() {
  size(1500, 1000);

  data = new FloatTable("clean_data_tsv.tsv");
  numRow = data.getRowCount();
  numCol = data.getColumnCount();

  yearArray = int(data.getRowNames());
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
  smooth();
}

//draw function
void draw() {
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
  fill(#c16956);
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
