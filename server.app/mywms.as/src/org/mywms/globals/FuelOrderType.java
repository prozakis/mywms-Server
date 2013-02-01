package org.mywms.globals;

public enum FuelOrderType {
    VEHICLE_REFUEL("VEHICLE_REFUEL"),
    SERVICE_NOTE("SERVICE_NOTE");

private final String label;

  private FuelOrderType(String label) {
    this.label = label;
  }

  public String getLabel() {
    return this.label;
  }

}
