import pytest

from experiments.scripts import demo


@pytest.mark.parametrize("flags", [[], ["--offline"]])
def test_dry_run_lists_all_steps_without_execution_or_pause(monkeypatch, capsys, flags):
    monkeypatch.setattr(demo, "step", lambda *args: pytest.fail("Executed dry-run step"))
    monkeypatch.setattr("builtins.input", lambda *args: pytest.fail("Paused with --no-pause"))
    assert demo.main(["--no-pause", "--dry-run", *flags]) == 0
    output = capsys.readouterr().out
    assert [line.split(" — ")[0] for line in output.splitlines() if line.startswith("Bước ")] == [
        f"Bước {i}/10" for i in range(1, 11)
    ]
    assert "--demo --start-suite test-suites/smoke" in output
    assert ("--offline" in output) == bool(flags)
