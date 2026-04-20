// =============================================================================
// Pipeline Jenkins — Build et publication APK Sentinelle
//
// Job : sentinelle-apk
//
// Paramètres :
//   BRANCH       → Branche Git à builder (défaut : origin/master)
//   BUILD_TYPE   → Type de build (debug / release)  — debug par défaut
//
// Livrables :
//   /opt/projet/uitguard.com/site/assets/apk/sentinelle-v<version>.apk
//   /opt/projet/uitguard.com/site/assets/apk/sentinelle-latest.apk  (symlink)
//   /opt/projet/uitguard.com/site/assets/apk/sentinelle.json        (manifest)
//
// Credentials Jenkins :
//   9cc29867-a50e-48d3-9ca7-b1a0e2319481  → Git (username/password)
// =============================================================================

pipeline {
    agent any

    parameters {
        gitParameter(
            name: 'BRANCH',
            type: 'PT_BRANCH',
            defaultValue: 'origin/master',
            selectedValue: 'DEFAULT',
            description: 'Branche Git à builder',
            sortMode: 'ASCENDING_SMART',
            branchFilter: '.*'
        )
        choice(
            name: 'BUILD_TYPE',
            choices: ['debug', 'release'],
            description: 'Type de build Android (release nécessite un keystore, non configuré pour l\'instant)'
        )
    }

    options {
        timeout(time: 20, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '5'))
        disableConcurrentBuilds()
        timestamps()
    }

    environment {
        PROJECT_DIR      = '/opt/projet/sentinelle'
        ANDROID_DIR      = '/opt/projet/sentinelle/android'
        SITE_APK_DIR     = '/opt/projet/uitguard.com/site/assets/apk'
        JAVA_HOME        = '/opt/softs/java/jdk-25.0.2+10'
        ANDROID_SDK_ROOT = '/opt/softs/android-sdk'
    }

    stages {

        // =====================================================================
        stage('Pull') {
        // =====================================================================
            steps {
                withCredentials([usernamePassword(
                    credentialsId: '9cc29867-a50e-48d3-9ca7-b1a0e2319481',
                    usernameVariable: 'GIT_USER',
                    passwordVariable: 'GIT_TOKEN'
                )]) {
                    sh """
                        cd ${env.PROJECT_DIR}
                        git remote set-url origin https://\${GIT_USER}:\${GIT_TOKEN}@github.com/ctardy/sentinelle.git
                        BRANCH_NAME=\$(echo "${params.BRANCH}" | sed 's|origin/||')
                        git stash
                        git fetch origin
                        git checkout \$BRANCH_NAME 2>/dev/null || git checkout -b \$BRANCH_NAME origin/\$BRANCH_NAME
                        git pull --rebase origin \$BRANCH_NAME
                        git stash pop 2>/dev/null || true
                        git remote set-url origin https://github.com/ctardy/sentinelle.git
                        echo "Branche : \$BRANCH_NAME"
                        echo "Commit  : \$(git log -1 --oneline)"

                        # local.properties pour la build Android
                        echo "sdk.dir=${env.ANDROID_SDK_ROOT}" > android/local.properties
                    """
                }
            }
        }

        // =====================================================================
        stage('Version') {
        // =====================================================================
            steps {
                sh """
                    cd ${env.ANDROID_DIR}
                    VERSION_CODE=\$(grep versionCode version.properties | cut -d= -f2)
                    VERSION_NAME=\$(grep versionName version.properties | cut -d= -f2)
                    echo "=== APK Sentinelle ==="
                    echo "Version    : \${VERSION_NAME}"
                    echo "Code       : \${VERSION_CODE}"
                    echo "Build type : ${params.BUILD_TYPE}"
                """
            }
        }

        // =====================================================================
        stage('Build APK') {
        // =====================================================================
            steps {
                sh """
                    cd ${env.ANDROID_DIR}
                    BUILD_TYPE="${params.BUILD_TYPE}"
                    BUILD_TYPE_CAP="\$(echo "\$BUILD_TYPE" | sed 's/./\\u&/')"
                    TASK="assemble\${BUILD_TYPE_CAP}"

                    echo "=== Tâche Gradle : \$TASK ==="
                    export JAVA_HOME=${env.JAVA_HOME}
                    export ANDROID_SDK_ROOT=${env.ANDROID_SDK_ROOT}
                    export PATH="\$JAVA_HOME/bin:\$PATH"

                    chmod +x ./gradlew
                    ./gradlew \$TASK --no-daemon --stacktrace

                    APK_PATH=\$(find app/build/outputs/apk/\$BUILD_TYPE -name "*.apk" | head -1)
                    if [ -z "\$APK_PATH" ]; then
                        echo "❌ APK introuvable !"
                        exit 1
                    fi
                    APK_SIZE=\$(du -h "\$APK_PATH" | cut -f1)
                    echo "=== APK générée ==="
                    echo "Chemin : \$APK_PATH"
                    echo "Taille : \$APK_SIZE"
                """
            }
        }

        // =====================================================================
        stage('Publish') {
        // =====================================================================
            steps {
                sh """
                    cd ${env.ANDROID_DIR}
                    BUILD_TYPE="${params.BUILD_TYPE}"
                    APK_PATH=\$(find app/build/outputs/apk/\$BUILD_TYPE -name "*.apk" | head -1)

                    VERSION_CODE=\$(grep versionCode version.properties | cut -d= -f2)
                    VERSION_NAME=\$(grep versionName version.properties | cut -d= -f2)

                    mkdir -p ${env.SITE_APK_DIR}

                    TARGET_APK="${env.SITE_APK_DIR}/sentinelle-v\${VERSION_NAME}.apk"
                    LATEST_APK="${env.SITE_APK_DIR}/sentinelle-latest.apk"

                    cp "\$APK_PATH" "\$TARGET_APK"

                    # Symlink latest (si le FS refuse les symlinks, fallback copie)
                    ln -sf "sentinelle-v\${VERSION_NAME}.apk" "\$LATEST_APK" \
                        || cp "\$TARGET_APK" "\$LATEST_APK"

                    # Manifest JSON lu par la carte du site
                    SHA256=\$(sha256sum "\$TARGET_APK" | cut -d' ' -f1)
                    SIZE_BYTES=\$(stat -c%s "\$TARGET_APK")
                    SIZE_HUMAN=\$(du -h "\$TARGET_APK" | cut -f1)
                    BUILT_AT=\$(date -u +%Y-%m-%dT%H:%M:%SZ)

                    cat > ${env.SITE_APK_DIR}/sentinelle.json <<JEOF
{
  "versionName": "\${VERSION_NAME}",
  "versionCode": \${VERSION_CODE},
  "buildType": "\${BUILD_TYPE}",
  "fileName": "sentinelle-v\${VERSION_NAME}.apk",
  "fileSize": \${SIZE_BYTES},
  "fileSizeHuman": "\${SIZE_HUMAN}",
  "sha256": "\${SHA256}",
  "builtAt": "\${BUILT_AT}",
  "url": "/assets/apk/sentinelle-v\${VERSION_NAME}.apk",
  "urlLatest": "/assets/apk/sentinelle-latest.apk"
}
JEOF

                    echo "=== Publication ==="
                    echo "APK     : \$TARGET_APK"
                    echo "Latest  : \$LATEST_APK"
                    echo "Taille  : \$SIZE_HUMAN"
                    echo "SHA256  : \$SHA256"
                    ls -lh ${env.SITE_APK_DIR}/
                """
            }
        }

    }

    post {
        always {
            sh """
                cd ${env.ANDROID_DIR} 2>/dev/null && ./gradlew --stop 2>/dev/null || true
            """
        }
        success {
            sh """
                VERSION_NAME=\$(grep versionName ${env.ANDROID_DIR}/version.properties | cut -d= -f2)
                VERSION_CODE=\$(grep versionCode ${env.ANDROID_DIR}/version.properties | cut -d= -f2)
                echo "========================================"
                echo " APK publiée : v\${VERSION_NAME} (code \${VERSION_CODE})"
                echo " Build type  : ${params.BUILD_TYPE}"
                echo " Lien stable : https://uitguard.com/assets/apk/sentinelle-latest.apk"
                echo "========================================"
            """
        }
    }
}
