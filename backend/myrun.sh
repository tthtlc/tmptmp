
###mvn spring-boot:run,

mvn site clean 

mvn site compile
mvn site install

###mvn -X site:run
mvn spring-boot:run

exit
mvn clean install 
mvn spring-boot:run
exit
pre-clean, clean, post-clean, validate, initialize, generate-sources, process-sources, generate-resources, process-resources, compile, process-classes, generate-test-sources, process-test-sources, generate-test-resources, process-test-resources, test-compile, process-test-classes, test, prepare-package, package, pre-integration-test, integration-test, post-integration-test, verify, install, deploy, pre-site, site, post-site, site-deploy.

usage: mvn [options] [<goal(s)>] [<phase(s)>]

Options:
 -am,--also-make                         If project list is specified,
                                         also build projects required by
                                         the list
 -amd,--also-make-dependents             If project list is specified,
                                         also build projects that depend
                                         on projects on the list
 -B,--batch-mode                         Run in non-interactive (batch)
                                         mode (disables output color)
 -b,--builder <arg>                      The id of the build strategy to
                                         use
 -C,--strict-checksums                   Fail the build if checksums don't
                                         match
 -c,--lax-checksums                      Warn if checksums don't match
    --color <arg>                        Defines the color mode of the
                                         output. Supported are 'auto',
                                         'always', 'never'.
 -cpu,--check-plugin-updates             Ineffective, only kept for
                                         backward compatibility
 -D,--define <arg>                       Define a user property
 -e,--errors                             Produce execution error messages
 -emp,--encrypt-master-password <arg>    Encrypt master security password
 -ep,--encrypt-password <arg>            Encrypt server password
 -f,--file <arg>                         Force the use of an alternate POM
                                         file (or directory with pom.xml)
 -fae,--fail-at-end                      Only fail the build afterwards;
                                         allow all non-impacted builds to
                                         continue
 -ff,--fail-fast                         Stop at first failure in
                                         reactorized builds
 -fn,--fail-never                        NEVER fail the build, regardless
                                         of project result
 -gs,--global-settings <arg>             Alternate path for the global
                                         settings file
 -gt,--global-toolchains <arg>           Alternate path for the global
                                         toolchains file
 -h,--help                               Display help information
 -itr,--ignore-transitive-repositories   If set, Maven will ignore remote
                                         repositories introduced by
                                         transitive dependencies.
 -l,--log-file <arg>                     Log file where all build output
                                         will go (disables output color)
 -llr,--legacy-local-repository          UNSUPPORTED: Use of this option
                                         will make Maven invocation fail.
 -N,--non-recursive                      Do not recurse into sub-projects
 -npr,--no-plugin-registry               Ineffective, only kept for
                                         backward compatibility
 -npu,--no-plugin-updates                Ineffective, only kept for
                                         backward compatibility
 -nsu,--no-snapshot-updates              Suppress SNAPSHOT updates
 -ntp,--no-transfer-progress             Do not display transfer progress
                                         when downloading or uploading
 -o,--offline                            Work offline
 -P,--activate-profiles <arg>            Comma-delimited list of profiles
                                         to activate
 -pl,--projects <arg>                    Comma-delimited list of specified
                                         reactor projects to build instead
                                         of all projects. A project can be
                                         specified by [groupId]:artifactId
                                         or by its relative path
 -q,--quiet                              Quiet output - only show errors
 -rf,--resume-from <arg>                 Resume reactor from specified
                                         project
 -s,--settings <arg>                     Alternate path for the user
                                         settings file
 -t,--toolchains <arg>                   Alternate path for the user
                                         toolchains file
 -T,--threads <arg>                      Thread count, for instance 4
                                         (int) or 2C/2.5C (int/float)
                                         where C is core multiplied
 -U,--update-snapshots                   Forces a check for missing
                                         releases and updated snapshots on
                                         remote repositories
 -up,--update-plugins                    Ineffective, only kept for
                                         backward compatibility
 -v,--version                            Display version information
 -V,--show-version                       Display version information
                                         WITHOUT stopping build
 -X,--debug                              Produce execution debug output

