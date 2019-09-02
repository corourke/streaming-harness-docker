import { Meteor } from 'meteor/meteor';

import { HealthChecks } from '../imports/api/healthChecks.js';

// Have to start meteor with: --settings settings.json
const kafkaServerIP = Meteor.settings.kafkaServerIP || "localhost";



Meteor.startup(() => {
  // code to run on server at startup
  HealthChecks.remove({});
  HealthChecks.insert({ component: "zookeeper", status: "UNK", createdAt: new Date() });
  HealthChecks.insert({ component: "kafka", status: "UNK", createdAt: new Date() });
  HealthChecks.insert({ component: "scan_generator", status: "UNK", createdAt: new Date() });

  console.log("kafkaServerIP: " + kafkaServerIP);

  // Zookeeper client
  // https://github.com/alexguan/node-zookeeper-client

  // const zookeeper = require('node-zookeeper-client');
  //
  // var zk_client = zookeeper.createClient(kafkaServerIP + ':2181');
  //
  // zk_client.once('connected', function() {
  //   console.log('Connected to zookeeper.');
  //   var zk_path = "/kperf/frontend";
  //
  //   zk_client.create("/kperf", zookeeper.CreateMode.EPHEMERAL,
  //     function(error, path) {
  //       if (error) {
  //         console.log('Failed to create node: %s due to: %s.', path, error);
  //         console.log(error);
  //       } else {
  //         console.log('Node: %s successfully created.', path);
  //       }
  //     });
  //
  //     //Meteor.setInterval(() => {
  //       console.log("Brokers: " + listChildren(zk_client, "/brokers/ids"));
  //     //}, 5000);
  //
  //   //client.close();
  // });
  //
  // zk_client.connect();
  //
  //
  // function listChildren(client, path) {
  //   var result;
  //   client.getChildren(
  //     path,
  //     function(event) {
  //       console.log('Got watcher event: %s', event);
  //       listChildren(client, path);
  //     },
  //     function(error, children, stat) {
  //       if (error) {
  //         console.log(
  //           'Failed to list children of node: %s due to: %s.',
  //           path,
  //           error
  //         );
  //         return;
  //       }
  //       result = children;
  //       console.log('Children of node: %s are: %j.', path, children);
  //     }
  //   );
  //
  //   // TODO: Ug, this is async
  //   return result;
  // }

  // Zookeeper Monitor
  // Using Nodejs net

  // const net = require('net');
  //
  // var zookeeper_status = "UNK";
  // Meteor.setInterval(() => {
  //   var client = new net.Socket();
  //   client.connect(2181, kafkaServerIP);
  //
  //   client.on('error', () => { zookeeper_status = "ERR"; })
  //
  //   client.on('connect', () => client.write('srvr'));
  //
  //   client.on('data', function(data) {
  //     zookeeper_status = (data.length != 0 ? "OK" : "UNK")
  //     client.destroy(); // kill client after server's response
  //   });
  //
  //   // Note: this is kind of jank because it is executed immediately as all the
  //   // client calls are asynchronous. But it sort of works.
  //   HealthChecks.update({component: 'zookeeper'}, { component: "zookeeper", status: zookeeper_status, createdAt: new Date() })
  // } , 5000);

});
