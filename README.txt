
 +----------------------------+
 | Bootstrapping and Building |
 +----------------------------+

Genesis currently must be bootstrapped, which will build all modules that are
used as extentions by config/project-config.

To perform a clean bootstrap simply run:

    ./bootstrap

To leave the local repository repository untouched then run:

    ./build

Both will run a multi-stage build.  When making changes it is recommended to
run `bootstrap` to ensure that no locally installed artifacts are interfearing
with a clean build.

