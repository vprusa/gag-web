package cz.muni.fi.gag.app.domain;

public enum SensorType {
	// TODO this is named QUAT-GYRO in the class diagram but javac does not permit 
	// enum instances with hyphens
    QUAT,
    MAG,
    ACC;
}