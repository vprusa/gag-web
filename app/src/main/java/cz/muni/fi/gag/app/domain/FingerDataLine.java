package cz.muni.fi.gag.app.domain;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class FingerDataLine extends DataLine {
	
	/* TODO we need to import the right library(ies) in order to import these classes!
    private Quaternion quat;
	
    private 3D acc;
	 */
	@Enumerated(EnumType.ORDINAL)
	private FingerPosition position;
}