# Alter "gestures"

add: 
```
match [%]
exec [String]
```

## exec:
(Type[c=cursor|k=keboard|m=macro])|

### Cursor

- J(=Set/Jump To Position)\[{X[int]},{Y[int]}\]
// - M(=Move){[L(=LEFT)|R(=RIGHT)|U(=UP)|D(=DOWN)]}{S(=Speed)[float]}
- A(=Move Angle)\[{R(=Rotation)[float_<0,360)]},{S(=Speed)[float]}\]

#### Buttons: 

{B(=Button)[L(=Left)|R(=Right)|M(=Middle)||F(=Forward)|B(=Back)]}

- C(=Click){B(=Button)}
- P(=Press){B(=Button)}
- R(=Release){B(=Button)}
- S(=Scroll){U(=UP),D(=Down)}{S(=Speed)[float]}

### Keyboard

Keyboard will most likely not work properly because of insufficient precision and lack of sensors on all fingers parts.
This will result in necessity of keyboard in (AR / VR) scene and usage (or implementation) of Collision System in (AR / VR) scene.
Possible predisposition for CS is bending of finger's visualizations parts relative to angle retrieved by sensor.

- K[TODO]


### Macro

Idk..
- press & release multiple keys at once or in giver order
- call some other use defined methods (JiT/REPR execution)?

- M[TODO] 

### TODOs

- Brainstorm if separation "exec" column to table Commands is desirable

# Add other stuff

#
