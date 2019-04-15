package cz.muni.fi.gag.app.web.domain;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;

@MappedSuperclass
public abstract class SensorOffset {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	protected long id;
	
    private short x;

    private short y;

    private short z;

    @ManyToOne
    @NotNull
    private HandDevice device;
    
    @Enumerated(EnumType.ORDINAL)
    private SensorType sensorType;
}