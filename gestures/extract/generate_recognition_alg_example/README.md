python confusion_matrix.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --ref-gest 1073 --in-gest 474



python gen.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --ref-gest 1073 --in-gest 479 --threshold 1.384604 | tee logs/gen.py.`now_str`.log




(.venv) vprusa@fedora:~/workspace/p/gag-web/gestures/extract/generate_recognition_alg_example$

 python gen.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --ref-gest 74 --in-gest 41 --threshold 1.384604 | tee logs/gen.py.`now_str`.log


python gen.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --ref-gest 1073 --in-gest 474 --threshold 0.384604 | tee logs/gen.py.`now_str`.log



1076

0.351444

python gen.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --ref-gest 1076 --in-gest 474 --threshold 0.384604 | tee logs/gen.py.`now_str`.log


--ref-gest-quat-order xyza

for i in 479 480 481 482 483 484 485 486 487 488  ; do python gen.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --ref-gest 1073 --in-gest $i --threshold 0.384604 --ref-gest-quat-order xyza ; done | tee logs/gen.py.`now_str`.log


python gen.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --ref-gest 1073 --in-gest $i --threshold 0.384604 --ref-gest-quat-order xyza | tee logs/gen.py.`now_str`.log


python gen.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --ref-gest 1080 --in-gest 474 --threshold 0.384604 --ref-gest-quat-order xyza | tee logs/gen.py.`now_str`.log

465 .. +10

471

python gen.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --ref-gest 1073 --in-gest 1053 --threshold 0.384604 --ref-gest-quat-order xyza -v | tee logs/gen.py.`now_str`.log


python gen.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --ref-gest 1076 --in-gest 474 --threshold 0.384604 | tee logs/gen.py.`now_str`.log
python gen.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --ref-gest 1073 --in-gest 474 --threshold 0.384604 | tee logs/gen.py.`now_str`.log



(.venv) vprusa@fedora:~/workspace/p/gag-web/gestures/extract/generate_recognition_alg_example$ python gen.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --ref-gest 1073 --in-gest 474 --threshold 0.384604 --delay 0.5 --ref-gest-quat-order xyza | tee logs/gen.py.`now_str`.log

\begin{table}[ht]
    \centering
    \resizebox{\textwidth}{!}{%
    \begin{tabular}{c c c c c c c c}
        \toprule
        Čas (2025-04-25 18:02) & qw & qx & qy & qz & Index shody & Částečná shody \
        \midrule
            13.985 & 0.979065 & 0.119446 & -0.050110 & -0.156738 & - & 0:1 \\ 
            14.132 & 0.979065 & 0.119385 & -0.050171 & -0.156982 & - & 0:2 \\ 
            14.280 & 0.979004 & 0.119385 & -0.050232 & -0.157166 & - & 0:2 \\ 
            14.472 & 0.978943 & 0.119507 & -0.050354 & -0.157532 & - & 0:2 \\ 
            14.618 & 0.978943 & 0.119446 & -0.050232 & -0.157715 & - & 0:2,1:1 \\ 
            14.766 & 0.981934 & 0.087524 & -0.036438 & -0.163574 & - & 0:2,1:2 \\ 
            14.911 & 0.986084 & 0.006531 & -0.001709 & -0.165771 & - & 0:3,1:3 \\ 
            15.058 & 0.980469 & -0.089966 & 0.059448 & -0.164246 & - & 0:3,1:3 \\ 
            15.251 & 0.971069 & -0.139526 & 0.104065 & -0.163330 & - & 0:3,1:3 \\ 
            15.397 & 0.966187 & -0.157959 & 0.123901 & -0.161682 & - & 0:3,1:3 \\ 
            15.545 & 0.965637 & -0.160583 & 0.126343 & -0.160461 & - & 0:3,1:3 \\ 
            15.692 & 0.966064 & -0.159485 & 0.123901 & -0.160950 & - & 0:3,1:3 \\ 
            15.838 & 0.966553 & -0.157593 & 0.120239 & -0.162354 & - & 0:3,1:3 \\ 
            15.983 & 0.966919 & -0.155945 & 0.117859 & -0.163696 & - & 0:3,1:3 \\ 
            16.178 & 0.968384 & -0.150146 & 0.109741 & -0.166199 & - & 0:3,1:3 \\ 
            16.372 & 0.974609 & -0.105713 & 0.070557 & -0.184021 & - & 0:3,1:3 \\ 
            16.472 & 0.980896 & 0.043701 & -0.021423 & -0.188171 & - & 0:4,1:4,2:1 \\ 
            16.618 & 0.977234 & 0.105774 & -0.049927 & -0.177002 & \textbf{0,1} & \textbf{2:2} \\ 
            16.813 & 0.974243 & 0.127747 & -0.059021 & -0.175964 & - & 2:2 \\ 
        \bottomrule
    \end{tabular}%
    }
    \caption{Gesture Recognition Results}
    \label{tab:gesture_recognition_results}
\end{table}



#