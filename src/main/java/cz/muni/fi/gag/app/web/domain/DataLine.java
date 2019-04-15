package cz.muni.fi.gag.app.web.domain;

import java.time.LocalTime;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;

@MappedSuperclass
public abstract class DataLine {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	protected long id;
	
	@NotNull
    private LocalTime timestamp;
    
    @ManyToOne
    @NotNull
    private Gesture gesture;
}