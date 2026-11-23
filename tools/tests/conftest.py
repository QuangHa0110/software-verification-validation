"""Pytest configuration for tools/tests."""


def pytest_configure(config):
    config.addinivalue_line("markers", "maven: integration tests that execute real Maven builds")
