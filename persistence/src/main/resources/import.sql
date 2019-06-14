
INSERT INTO Users (role, thirdPartyId) VALUES (1,'email:user@test.com')
INSERT INTO Users (role, thirdPartyId) VALUES (2,'email:admin2@test.com')

INSERT INTO gesture (dateCreated, userAlias, user_id) VALUES ('1970-01-01 00:00:01', 'testGesture1',1)
INSERT INTO handDevice (deviceId, user_id) VALUES ('uniqueDeviceId1', 1)

INSERT INTO gesture (dateCreated, userAlias, user_id) VALUES ('1970-01-01 00:00:01', 'testGesture2',2)
INSERT INTO handDevice (deviceId, user_id) VALUES ('uniqueDeviceId2', 2)

INSERT INTO gesture (dateCreated, userAlias, user_id) VALUES ('1970-01-01 00:00:01', 'testGesture3',3)
INSERT INTO handDevice (deviceId, user_id) VALUES ('uniqueDeviceId3', 3)

INSERT INTO DataLine (timestamp, gesture_id) VALUES ('1970-01-01 00:00:01', 1)
INSERT INTO FingerDataLine (id, accX, accY, accZ, position, quatA, quatX, quatY, quatZ) VALUES (1,1,1,1,0,1,1,1,1)
INSERT INTO WristDataLine (id, magX, magY, magZ) VALUES (1,1,1,1)

INSERT INTO SensorOffset (DTYPE, sensorType, x,y,z, position, offsets) VALUES ('Finger',0,0,0,0,0,1)
INSERT INTO SensorOffset (DTYPE, sensorType, x,y,z, offsets) VALUES ('Wrist',0,0,0,0,1)
