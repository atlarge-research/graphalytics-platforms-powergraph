for host in `echo $1 | tr ',' ' '`;
do
  ssh $host mkdir -p `dirname $2`
  scp $2 $host:$2
done
echo ${@:2}
mpirun --map-by ppr:1:node --bind-to none --mca btl ^usnic -v --report-bindings --host $1 --nolocal ${@:3} &

echo $! > $LOG_PATH/executable.pid
wait $!



















