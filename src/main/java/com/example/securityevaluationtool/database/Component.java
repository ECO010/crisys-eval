package com.example.securityevaluationtool.database;

import java.util.List;

// Integrate ICSA vulnerability DB as it has make model and known vulnerabilities
public class Component {
    public String componentType;
    public String makeAndModel;
    public String purdueLevel;
    public List<String> knownVulnerabilities;
    public String icsDomain; // ED, Water, Waste (should use an enum here)
}
