package io.stargate.sgv2.jsonapi.api.v1.util;

import io.restassured.specification.RequestSpecification;
import io.stargate.sgv2.jsonapi.api.model.command.CommandName;
import io.stargate.sgv2.jsonapi.api.v1.CollectionResource;

public class DataApiTableCommandSender extends DataApiCommandSenderBase<DataApiTableCommandSender> {
  private final String tableName;
  private final String keyspace;
  private TableTemplates templated;

  protected DataApiTableCommandSender(String keyspace, String tableName) {
    this.keyspace = keyspace;
    this.tableName = tableName;
    this.templated = new TableTemplates(this);
  }

  public TableTemplates templated() {
    return templated;
  }

  @Override
  protected io.restassured.response.Response postInternal(RequestSpecification request) {
    return request.post(CollectionResource.BASE_PATH, keyspace, tableName);
  }

  public DataApiResponseValidator postUpdateOne(String jsonClause) {
    return postCommand(CommandName.UPDATE_ONE, jsonClause);
  }

  public DataApiResponseValidator postDeleteMany(String jsonClause) {
    return postCommand(CommandName.DELETE_MANY, jsonClause);
  }

  public DataApiResponseValidator postDeleteOne(String jsonClause) {
    return postCommand(CommandName.DELETE_ONE, jsonClause);
  }

  /**
   * Partially typed method for sending a POST command to the Data API: caller is responsible for
   * formatting the clause to include as (JSON Object) argument of "finOne" command.
   *
   * @param `findOneClause` JSON clause to include in the "findOne" command: minimally empty JSON
   *     Object ({@code { } })
   * @return Response validator for further assertions
   */
  public DataApiResponseValidator postFindOne(String jsonClause) {
    return postCommand(CommandName.FIND_ONE, jsonClause);
  }

  public DataApiResponseValidator postFind(String jsonClause) {
    return postCommand(CommandName.FIND, jsonClause);
  }

  public DataApiResponseValidator postFindWithFilter(String filter) {
    var jsonClause = "{ \"filter\": " + filter + " } ";
    return postCommand(CommandName.FIND, jsonClause);
  }

  public DataApiResponseValidator postInsertOne(String jsonClause) {
    return postCommand(CommandName.INSERT_ONE, jsonClause);
  }

  public DataApiResponseValidator postInsertMany(String jsonClause) {
    return postCommand(CommandName.INSERT_MANY, jsonClause);
  }

  public DataApiResponseValidator postCreateIndex(String jsonClause) {
    return postCommand(CommandName.CREATE_INDEX, jsonClause);
  }

  public DataApiResponseValidator postListIndexes(String jsonClause) {
    return postCommand(CommandName.LIST_INDEXES, jsonClause);
  }

  public DataApiResponseValidator postCreateTextIndex(String jsonClause) {
    return postCommand(CommandName.CREATE_TEXT_INDEX, jsonClause);
  }

  public DataApiResponseValidator postCreateVectorIndex(String jsonClause) {
    return postCommand(CommandName.CREATE_VECTOR_INDEX, jsonClause);
  }

  public DataApiResponseValidator postAlterTable(String tableDefAsJSON) {
    return postCommand(CommandName.ALTER_TABLE, tableDefAsJSON);
  }

  public DataApiResponseValidator postCount() {
    return postCommand(CommandName.COUNT_DOCUMENTS, "{}");
  }
}
