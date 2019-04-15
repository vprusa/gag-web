package cz.muni.fi.gag.app.web.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

@Entity
public class HandDevice {
    
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	protected long id;
	
	@NotNull
	@Column(nullable = false)
	private String deviceId;

	@ManyToOne
	@NotNull
    private User user;

    @OneToMany(mappedBy = "device", orphanRemoval = true)
    private List<SensorOffset> offset = new ArrayList<>();
}