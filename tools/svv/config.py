from pathlib import Path

import yaml

ROOT = Path(__file__).resolve().parents[2]


def _overlay(base: dict, updates: dict) -> None:
    for key, value in updates.items():
        if isinstance(value, dict) and isinstance(base.get(key), dict):
            _overlay(base[key], value)
        else:
            base[key] = value


def load_config(directory: Path = ROOT / "experiments/config") -> dict:
    """Merge default.yaml with *.local.yaml in filename order; lists replace lists."""
    config = {}
    for path in [directory / "default.yaml", *sorted(directory.glob("*.local.yaml"))]:
        data = yaml.safe_load(path.read_text(encoding="utf-8"))
        if not isinstance(data, dict):
            raise ValueError(f"{path}: expected a YAML mapping")
        _overlay(config, data)
    return config
