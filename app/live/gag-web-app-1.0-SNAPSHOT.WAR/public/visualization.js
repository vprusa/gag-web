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
}

// Hand Visualization
class HandVisualization {
    constructor(side, scene) {
        this.side = side;
        this.scene = scene;
        this.group = new THREE.Group();
        this.scene.add(this.group);

        this.thumb = new FingerVisualization(scene, 'Thumb');
        this.index = new FingerVisualization(scene, 'Index');
        this.middle = new FingerVisualization(scene, 'Middle');
        this.ring = new FingerVisualization(scene, 'Ring');
        this.little = new FingerVisualization(scene, 'Little');

        this.group.add(this.thumb.group, this.index.group, this.middle.group, this.ring.group, this.little.group);
    }

    rotate(q) {
        this.group.setRotationFromQuaternion(new THREE.Quaternion(q.x, q.y, q.z, q.w));
    }

    bendFinger(finger, angle) {
        if (this[finger]) {
            this[finger].bend(angle);
        }
    }
}

// Finger Visualization
class FingerVisualization {
    constructor(scene, name) {
        this.scene = scene;
        this.name = name;
        this.group = new THREE.Group();
        this.segments = [];

        let prevSegment = null;
        for (let i = 0; i < 4; i++) {
            const segment = this.createSegment();
            if (prevSegment) {
                segment.position.y = 15;
                prevSegment.add(segment);
            } else {
                this.group.add(segment);
            }
            prevSegment = segment;
            this.segments.push(segment);
        }
        this.scene.add(this.group);
    }

    createSegment() {
        const geometry = new THREE.BoxGeometry(5, 20, 5);
        const material = new THREE.MeshBasicMaterial({ color: 0x00ff00 });
        return new THREE.Mesh(geometry, material);
    }

    bend(angle) {
        this.segments.forEach((segment, index) => {
            segment.rotation.x = angle / (index + 1);
        });
    }
}

// Visualization Scene
class VisualizationScene {
    constructor(container, width, height) {
        this.container = container;
        this.width = width;
        this.height = height;
        this.scene = new THREE.Scene();
        this.camera = new THREE.PerspectiveCamera(75, width / height, 0.1, 1000);
        this.renderer = new THREE.WebGLRenderer();
        this.renderer.setSize(width, height);
        this.container.appendChild(this.renderer.domElement);

        this.light = new THREE.DirectionalLight(0xffffff, 2);
        this.light.position.set(1, 1, 1).normalize();
        this.scene.add(this.light);

        this.leftHand = new HandVisualization(Hand.LEFT, this.scene);
        this.rightHand = new HandVisualization(Hand.RIGHT, this.scene);
        this.leftHand.group.position.set(-50, 0, 0);
        this.rightHand.group.position.set(50, 0, 0);

        this.animate();
    }

    animate() {
        requestAnimationFrame(() => this.animate());
        this.renderer.render(this.scene, this.camera);
    }

    rotateHand(hand, quaternion) {
        if (hand === Hand.LEFT) {
            this.leftHand.rotate(quaternion);
        } else {
            this.rightHand.rotate(quaternion);
        }
    }

    bendFinger(hand, finger, angle) {
        if (hand === Hand.LEFT) {
            this.leftHand.bendFinger(finger, angle);
        } else {
            this.rightHand.bendFinger(finger, angle);
        }
    }
}

export { VisualizationScene, HandVisualization, FingerVisualization, Quaternion, Hand, Sensor };
