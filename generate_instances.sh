#!/bin/bash

# Batch generator for NK landscape instances
# Runs NKLandscapeFwhtRunner 20 times for each (n,k) pair

# Define the (n, k) pairs
declare -a PAIRS=("1000:2" "1000:3" "1000:4" "500:3")

NUM_INSTANCES=20
BASE_DIR="./statistic"

echo "=== Batch NK Landscape Instance Generator ==="
echo "Base output directory: $BASE_DIR"
echo "Instances per (n,k) pair: $NUM_INSTANCES"
echo ""

# Build the project first
echo "Building project..."
mvn clean package -DskipTests=true > /dev/null 2>&1
if [ $? -ne 0 ]; then
    echo "Build failed!"
    exit 1
fi
echo "Build successful!"
echo ""

# Generate instances for each pair
for pair in "${PAIRS[@]}"; do
    IFS=':' read -r n k <<< "$pair"
    folder_name="n${n}_k${k}"
    folder_path="$BASE_DIR/$folder_name"
    
    echo "Generating $NUM_INSTANCES instances for (n=$n, k=$k)"
    echo "Output folder: $folder_path"
    
    # Create folder
    mkdir -p "$folder_path"
    
    start_time=$(date +%s)
    
    # Generate 20 instances
    for instance in $(seq 1 $NUM_INSTANCES); do
        instance_num=$(printf "%02d" $instance)
        csv_file="$folder_path/instance_$instance_num.csv"
        
        # Run with fixed seed for reproducibility
        java -cp target/classes com.walshtransform.NKLandscapeFwhtRunner "$n" "$k" "$csv_file" > /dev/null 2>&1
        
        if [ $? -eq 0 ]; then
            if [ $((instance % 5)) -eq 0 ] || [ "$instance" -eq 1 ]; then
                echo "  - Instance $instance_num generated"
            fi
        else
            echo "  ERROR: Failed to generate instance $instance_num"
            exit 1
        fi
    done
    
    end_time=$(date +%s)
    elapsed=$((end_time - start_time))
    echo "  Completed in ${elapsed}s"
    echo ""
done

echo "=== Generation Complete ==="
