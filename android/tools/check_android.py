"""APK-free Android compilation, lint, JVM tests and token checks."""
import os
from pathlib import Path
import subprocess
import sys
import xml.etree.ElementTree as ET

from verify_tokens import check


def main() -> None:
    root = Path(__file__).resolve().parents[2]
    check(root)
    env = os.environ.copy()
    if not env.get('JAVA_HOME') and sys.platform == 'darwin':
        env['JAVA_HOME'] = subprocess.check_output(
            ['/usr/libexec/java_home', '-v', '21'], text=True).strip()
    sdk = Path(env.get('ANDROID_HOME') or env.get('ANDROID_SDK_ROOT')
               or Path.home() / 'Library/Android/sdk')
    if not (sdk / 'platforms/android-36/android.jar').exists():
        raise RuntimeError('Install Android SDK Platform 36; set ANDROID_HOME.')
    env['ANDROID_HOME'] = str(sdk)
    subprocess.run([
        './gradlew', '--offline', '--no-daemon', '--console=plain',
        ':app:lintDebug', ':app:compileDebugKotlin', ':app:testDebugUnitTest',
    ], cwd=root / 'android', env=env, check=True)
    reports = list((root / 'android/app/build/test-results/testDebugUnitTest').glob('TEST-*.xml'))
    required = {
        'az.qida.app.domain.ContractValidationTest',
        'az.qida.app.data.local.UserPersistenceTest',
        'az.qida.app.data.local.PlanTransactionTest',
        'az.qida.app.data.local.DatabaseMigrationTest',
        'az.qida.app.data.local.CatalogIsolationTest',
        'az.qida.app.data.local.LibraryImportTest',
    }
    executed = set()
    test_count = 0
    for report in reports:
        suite = ET.parse(report).getroot()
        if any(int(suite.get(key, '0')) for key in ('failures', 'errors', 'skipped')):
            raise RuntimeError(f'Failed or skipped JVM tests: {report}')
        for case in suite.findall('testcase'):
            executed.add(case.get('classname'))
            test_count += 1
    if required - executed:
        raise RuntimeError(f'Required JVM suites did not execute: {sorted(required - executed)}')
    print(f'PASS: {test_count} JVM contract/Room tests; no failures, errors or skips')
    print('PASS: Android lint, Kotlin compilation, JVM tests and token checks (no APK)')
    print('NOT RUN: APK packaging, manifest badging and device launch/recreation smoke')


if __name__ == '__main__':
    main()
