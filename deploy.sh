#! /bin/sh

scp -r keys/ corn:~/cs244/keys
scp out/artifacts/cs244_jar/cs244.jar corn:~/cs244/.
scp cluster_config.json corn:~/cs244/.
