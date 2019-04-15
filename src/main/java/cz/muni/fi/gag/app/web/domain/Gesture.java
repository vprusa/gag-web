package cz.muni.fi.gag.app.web.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

@Entity
public class Gesture {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	protected long id;
	
	@NotNull
    private LocalDateTime dateCreated;

    @NotNull
    private String userAlias;

    @ManyToOne
    @NotNull
    private User user;
    
    @OneToMany(mappedBy = "gesture", orphanRemoval = true)
    private List<DataLine> data = new ArrayList<>();
}