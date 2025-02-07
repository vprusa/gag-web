import * as THREE from 'three';

// Hand Enumeration
const Hand = Object.freeze({ LEFT: 'left', RIGHT: 'right' });

// Sensor Enumeration
const Sensor = Object.freeze({
    THUMB: 0,
    INDEX: 1,
    MIDDLE: 2,
    RING: 3,
    LITTLE: 4,
    WRIST: 5
});

// Quaternion Class using Three.js
class Quaternion extends THREE.Quaternion {
    constructor(x, y, z, w) {
        super(x, y, z, w);
    }

    toEuler() {
        const euler = new THREE.Euler();
        euler.setFromQuaternion(this);
        return {
            yaw: THREE.MathUtils.radToDeg(euler.y),
            pitch: THREE.MathUtils.radToDeg(euler.x),
            roll: THREE.MathUtils.radToDeg(euler.z)
        };
    }
}

// VisualizationContext for Three.js manipulations
class VisualizationContext {
    _add(object, x, y, z) {
        object.position.set(x, y, z);
        return object;
    }

    _point(x, y, z, parent) {
        const geometry = new THREE.SphereGeometry(5, 32, 32);
        const material = new THREE.MeshBasicMaterial({ color: 0xffff00 });
        const sphere = new THREE.Mesh(geometry, material);
        sphere.position.set(x, y, z);
        if (parent) parent.add(sphere);
        return sphere;
    }

    _line(x1, y1, z1, x2, y2, z2, parent) {
        const material = new THREE.LineBasicMaterial({ color: 0xff33ff });
        const points = [new THREE.Vector3(x1, y1, z1), new THREE.Vector3(x2, y2, z2)];
        const geometry = new THREE.BufferGeometry().setFromPoints(points);
        const line = new THREE.Line(geometry, material);
        if (parent) parent.add(line);
        return line;
    }

    _rotateGeoms(quaternion, pivot) {
        if (pivot) pivot.setRotationFromQuaternion(quaternion);
    }
}

// Logger
class Logger {
    static dump(msg) {
        console.log(msg);
    }
}

// Hand Visualization
class HandVisualization {
    constructor(side, scene) {
        this.side = side;
        this.scene = scene;
        this.group = new THREE.Group();
        this.scene.add(this.group);

        this.thumb = new FingerVisualization(scene);
        this.index = new FingerVisualization(scene);
        this.middle = new FingerVisualization(scene);
        this.ring = new FingerVisualization(scene);
        this.little = new FingerVisualization(scene);

        this.group.add(this.thumb.group, this.index.group, this.middle.group, this.ring.group, this.little.group);
    }

    setRotationFromQuaternions(quaternionData) {
        let q = new THREE.Quaternion(quaternionData.x, quaternionData.y, quaternionData.z, quaternionData.w);
        this.group.setRotationFromQuaternion(q);
    }
}

// Finger Visualization
class FingerVisualization {
    constructor(scene) {
        this.scene = scene;
        this.group = new THREE.Group();

        this.segment1 = this.createSegment();
        this.segment2 = this.createSegment();
        this.segment3 = this.createSegment();
        this.segment4 = this.createSegment();

        this.group.add(this.segment1, this.segment2, this.segment3, this.segment4);
        this.scene.add(this.group);
    }

    createSegment() {
        const geometry = new THREE.BoxGeometry(5, 20, 5);
        const material = new THREE.MeshBasicMaterial({ color: 0x00ff00 });
        return new THREE.Mesh(geometry, material);
    }
}

// Visualization Scene
class VisualizationScene {
    constructor(container, width, height, numberOfHandsPairs = 1) {
        this.container = container;
        this.width = width;
        this.height = height;
        this.numberOfHandsPairs = numberOfHandsPairs;
        this.scene = new THREE.Scene();
        this.camera = new THREE.PerspectiveCamera(75, width / height, 0.1, 1000);
        this.renderer = new THREE.WebGLRenderer();
        this.renderer.setSize(width, height);
        this.container.appendChild(this.renderer.domElement);

        this.light = new THREE.DirectionalLight(0xffffff, 2);
        this.light.position.set(1, 1, 1).normalize();
        this.scene.add(this.light);

        this.hands = [];
        for (let i = 0; i < this.numberOfHandsPairs; i++) {
            this.hands.push([
                new HandVisualization(Hand.LEFT, this.scene),
                new HandVisualization(Hand.RIGHT, this.scene)
            ]);
        }

        this.animate();
    }

    animate() {
        requestAnimationFrame(() => this.animate());
        this.renderer.render(this.scene, this.camera);
    }

    updateAngles(handsPairIndex, quaternions) {
        const [leftHand, rightHand] = this.hands[handsPairIndex];
        leftHand.setRotationFromQuaternions(quaternions.left);
        rightHand.setRotationFromQuaternions(quaternions.right);
    }
}

export { VisualizationScene, HandVisualization, FingerVisualization, VisualizationContext, Quaternion, Hand, Sensor, Logger };
