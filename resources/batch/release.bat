pushd ../..

mvn release:prepare -P release-artifacts
::release:perform  

popd