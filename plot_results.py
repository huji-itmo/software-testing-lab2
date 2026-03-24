#!/usr/bin/env python3
import subprocess
import pandas as pd
import matplotlib.pyplot as plt
import os
import glob

OUTPUT_DIR = "output"


def run_jar():
    for f in glob.glob(os.path.join(OUTPUT_DIR, "*.csv")):
        os.remove(f)

    cmd = [
        "./gradlew",
        "run",
        "--args=--start -10.0 --end 10.0 --step 0.1 --precision 1e-10",
    ]
    result = subprocess.run(cmd, capture_output=True, text=True)
    print(result.stdout)
    if result.returncode != 0:
        print(result.stderr)
        raise RuntimeError("Failed to run jar")


def plot_results():
    csv_files = [
        "sin_results.csv",
        "cos_results.csv",
        "tan_results.csv",
        "sec_results.csv",
        "csc_results.csv",
        "ln_results.csv",
        "log_2.0_results.csv",
        "log_10.0_results.csv",
        "log_3.0_results.csv",
        "piecewise_results.csv",
    ]

    fig, axes = plt.subplots(3, 4, figsize=(16, 12))
    axes = axes.flatten()

    for i, csv_file in enumerate(csv_files):
        filepath = os.path.join(OUTPUT_DIR, csv_file)
        if os.path.exists(filepath):
            df = pd.read_csv(filepath)
            df = df.replace([float("inf"), float("-inf")], float("nan"))
            df = df.dropna()
            df = df.sort_values(by=df.columns[0])
            ax = axes[i]
            ax.plot(
                df.iloc[:, 0], df.iloc[:, 1], marker=".", linestyle="-", markersize=2
            )
            ax.set_title(csv_file.replace("_results.csv", ""))
            ax.set_xlabel("X")
            ax.set_ylabel("Y")
            ax.grid(True)
        else:
            axes[i].set_title(f"{csv_file} (not found)")

    for j in range(len(csv_files), len(axes)):
        axes[j].axis("off")

    plt.tight_layout()
    plt.savefig("results_plot.png", dpi=150)
    print("Plot saved to results_plot.png")


if __name__ == "__main__":
    run_jar()
    plot_results()
