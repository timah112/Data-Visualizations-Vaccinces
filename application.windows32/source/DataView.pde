//this allows the user to scroll over data points and for that information to be displayed 
void drawShowDetails(int col) {
  for ( int row = 0; row < numRow; row++ ) {
    if (data.isValid(row, col) ) {
      float value = data.getFloat(row, col);
      float x = map(yearArray[row], startYear, endYear, x1, x2);
      float y = map(value, minDataValue, maxDataValue, y2, y1);
      if ( dist(mouseX, mouseY, x, y) < 6) {
        stroke(#3735da);
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
void drawTabTitles() {
  fill(0);
  textSize(20);
  textAlign(LEFT);
  String title = data.getColumnName(currentColumn);
  text(title, x1, y1 - 10);
}

//this draws the tabs accoerding to how the columns in the data 
void drawTabs() {
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
void mousePressed() {
  if (mouseY > topTabValue && mouseY < bottomTabValue) {
    for (int col = 0; col < numCol; col++) {
      if (mouseX > leftTabValue[col] && mouseX < rightTabValue[col]) {
        setCurrent(col);
      }
    }
  }
}

//toggling lines on and off
void keyPressed() {
  if (key == ' ') {
    if (showLine == 0) {
      showLine = 1;
    } else {
      showLine = 0;
    }
  }
}

//determining current column
void setCurrent(int col) {
  currentColumn = col;
}

void drawYAxisLabels() {
  fill(0);
  textSize(20);
  textLeading(20);

  textAlign(CENTER, CENTER);
  text("Percent\nVaccinated", labelX, (y1+y2)/2);
  textAlign(CENTER);
  text("Year", (x1+x2)/2, labelY);
}

//drawing x-axis and its labels
void drawXAxisLabels() {
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


void drawPercentLabels() {
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
void drawDataPoints(int col) { 
  for (int row = 0; row < numRow; row++) { 
    strokeWeight(15);

    if (data.isValid(row, col) ) {
      float value = data.getFloat(row, col);  

      if (value >= 90) {
        stroke(#35da37);
        float x = map(yearArray[row], startYear, endYear, x1, x2);
        float y = map(value, minDataValue, maxDataValue, y2, y1);
        point(x, y);
      } else {
        stroke(#EC5166);
        float x = map(yearArray[row], startYear, endYear, x1, x2);
        float y = map(value, minDataValue, maxDataValue, y2, y1);
        point(x, y);
      }
    }
  }
}


//draw the data line
void drawData(int col) {
  beginShape();
  stroke(#020e03);
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
void drawDataTitle() {
  textSize(25);
  text("Immunization Rates in United States", 550, y1-50);
}
//drawing the key and information
void drawDisplayKey() {
  textSize(20);
  stroke(#03020e);
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
