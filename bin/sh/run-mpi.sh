for host in `echo $1 | tr ',' ' '`;
do
  ssh $host mkdir -p `dirname $3`
  scp $3 $host:$3
done

LOG_PATH=$2
echo ${@:3}
mpirun --map-by ppr:1:node --bind-to none --mca btl ^usnic -v --report-bindings --host $1 --nolocal ${@:3} &

echo $! > $LOG_PATH/executable.pid
wait $!



















