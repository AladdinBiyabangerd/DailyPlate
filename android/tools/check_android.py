"""APK-free Android compilation, lint, JVM tests and token checks."""
import os
from pathlib import Path
import subprocess
import sys

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
    print('PASS: Android lint, Kotlin compilation, JVM tests and token checks (no APK)')
    print('NOT RUN: APK packaging, manifest badging and device launch/recreation smoke')


if __name__ == '__main__':
    main()
