BINDIR=`dirname $0`
export CLASSPATH=$CLASSPATH:$BINDIR/../target/classes
# Note sequence count must match ie., 80,svr1:80,svr2:80 90,svr1:90,svr2:90
java org.jbundle.util.other.loaddist.sticky.StickyLoadDist localhost:80,localhost:8080
