#!/bin/bash

clusters_names=$(kubectl config get-contexts -o=name)

for cluster in $clusters_names
do

  cluster_version=$(kubectl version --context=$cluster --short | awk 'END{print $3}')

  if [ "$(echo $cluster_version | cut -c2- | cut -d. -f1)" -lt 1 ] || [ "$(echo $cluster_version | cut -c2- | cut -d. -f2)" -lt 16 ]; then


    readarray -t pods <<< "$(kubectl get pods --context=$cluster --all-namespaces -o jsonpath='{range .items[*]}{"\n"}{.metadata.name}{":\t"}{range .spec.containers[*]}{.image}{", "}{end}{end}' | sort | grep -e ":v2.3.*" | awk -F', ' '{print $1, $2}' | sed 's/,$//')"
   

    for pod in "${pods[@]}"
    do
      pod_name=$(echo $pod | cut -d ':' -f1)
      pod_image=$(echo $pod | | awk '{print $2}')
      new_image=$(echo $pod_image | sed 's/2\.3\./2.4./g')
	  
      echo "pod_name: $pod_name"
      echo "pod_image: $pod_image"
      echo "new_image: $new_image"

      echo "upgrading image in pod $pod_name in cluster $cluster"

      kubectl set image --context=$cluster -n $(echo $pod_name | cut -d '-' -f1) $pod_name $pod_image=$new_image
      sleep 15
    done


    fi
done
