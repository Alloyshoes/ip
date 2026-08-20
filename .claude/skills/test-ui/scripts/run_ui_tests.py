#!/usr/bin/env python3
"""
Compile the program and run every test case in test/ui-test-plan.md against
it, feeding each case's Input to stdin and comparing the program's full
stdout against its Expected output.

    python3 .claude/skills/test-ui/scripts/run_ui_tests.py

Stops at the first failing test case and reports the actual vs. expected
output. Exits 0 if every test case (in file order) passes, 1 otherwise.
"""
from __future__ import annotations

import argparse
import re
import subprocess
import sys
from pathlib import Path

# Adjust here if the project's entry class, source layout, or test plan
# location ever change.
SRC_DIR = "src/main/java"
MAIN_CLASS = "Eve"
CLASSES_DIR = "_temp/test-ui-classes"
PLAN_FILE = "test/ui-test-plan.md"

CASE_RE = re.compile(
    r"^## Test \d+: (?P<name>.+?)\s*$"
    r".*?"
    r"\*\*Aim:\*\*\s*(?P<aim>.+?)\s*\n\n"
    r"\*\*Input:\*\*\s*```(?:\w*)\n(?P<input>.*?)```"
    r".*?"
    r"\*\*Expected output:\*\*\s*```(?:\w*)\n(?P<expected>.*?)```",
    re.DOTALL | re.MULTILINE,
)


class TestCase:
    def __init__(self, name: str, aim: str, stdin: str, expected: str):
        self.name = name
        self.aim = aim
        self.stdin = stdin
        self.expected = expected


def parse_plan(text: str) -> list[TestCase]:
    cases = []
    for match in CASE_RE.finditer(text):
        cases.append(TestCase(
            name=match.group("name"),
            aim=match.group("aim"),
            stdin=match.group("input"),
            expected=match.group("expected"),
        ))
    return cases


def compile_program(repo: Path) -> None:
    sources = sorted((repo / SRC_DIR).glob("*.java"))
    if not sources:
        raise RuntimeError(f"no .java files found under {SRC_DIR}")
    classes_dir = repo / CLASSES_DIR
    classes_dir.mkdir(parents=True, exist_ok=True)
    result = subprocess.run(
        ["javac", "-d", str(classes_dir), *[str(s) for s in sources]],
        capture_output=True, text=True,
    )
    if result.returncode != 0:
        raise RuntimeError(f"compilation failed:\n{result.stdout}{result.stderr}")


def run_case(repo: Path, case: TestCase) -> tuple[str, str, int]:
    """Returns (stdout, stderr, returncode)."""
    result = subprocess.run(
        ["java", "-cp", str(repo / CLASSES_DIR), MAIN_CLASS],
        input=case.stdin, capture_output=True, text=True, timeout=10,
    )
    return result.stdout, result.stderr, result.returncode


def print_session(case: TestCase, stdout: str) -> None:
    print(f"--- Input (stdin) ---")
    for line in case.stdin.splitlines():
        print(f"> {line}")
    print(f"--- Actual output ---")
    print(stdout, end="" if stdout.endswith("\n") else "\n")


def main(argv: list[str]) -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--repo", default=".", help="repository root (default: cwd)")
    parser.add_argument("--plan", default=PLAN_FILE, help="path to the test plan, relative to --repo")
    args = parser.parse_args(argv)

    repo = Path(args.repo).resolve()
    plan_path = repo / args.plan
    if not plan_path.exists():
        print(f"error: test plan not found at {plan_path}", file=sys.stderr)
        return 1

    cases = parse_plan(plan_path.read_text(encoding="utf-8"))
    if not cases:
        print(f"error: no test cases found in {plan_path}", file=sys.stderr)
        return 1

    print(f"Compiling {SRC_DIR} ...")
    try:
        compile_program(repo)
    except RuntimeError as exc:
        print(f"error: {exc}", file=sys.stderr)
        return 1
    print(f"Found {len(cases)} test case(s) in {args.plan}\n")

    for index, case in enumerate(cases, start=1):
        print(f"=== Test {index}: {case.name} ===")
        print(f"Aim: {case.aim}")
        try:
            stdout, stderr, returncode = run_case(repo, case)
        except subprocess.TimeoutExpired:
            print("Result: FAIL (program did not exit within 10s -- "
                  "does the Input end with 'bye'?)")
            return 1

        print_session(case, stdout)
        if stderr:
            print(f"--- Actual stderr ---")
            print(stderr, end="" if stderr.endswith("\n") else "\n")

        actual_normalized = stdout.rstrip("\n")
        expected_normalized = case.expected.rstrip("\n")
        if returncode == 0 and actual_normalized == expected_normalized:
            print("Result: PASS\n")
            continue

        print("Result: FAIL")
        print("--- Expected output ---")
        print(case.expected, end="" if case.expected.endswith("\n") else "\n")
        print("--- Actual output ---")
        print(stdout, end="" if stdout.endswith("\n") else "\n")
        print(f"\nStopped at Test {index} of {len(cases)}: {case.name}")
        return 1

    print(f"All {len(cases)} test case(s) passed.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main(sys.argv[1:]))
