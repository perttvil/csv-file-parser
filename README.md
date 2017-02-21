# CSV Parser

## Usage
 
```
lein uberjar
java -jar target/uberjar/parser-0.1.0-SNAPSHOT-standalone.jar ./data/small-input.csv output.csv
```

## Purpose

Parses csv in format that is specified in file ./data/small-input.csv

Outputs a new CSV file where x and y coordinates and timestamp has been pivoted into single row.
Distance, duration and speed is counted using difference to the previous value.